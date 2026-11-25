
package com.courseregistration.service;

import com.courseregistration.dto.EnrollmentRequest;
import com.courseregistration.dto.EnrollmentResponse;
import com.courseregistration.entity.Course;
import com.courseregistration.entity.Enrollment;
import com.courseregistration.enums.EnrollmentStatus;
import com.courseregistration.exception.CourseNotFoundException;
import com.courseregistration.exception.DuplicateEnrollmentException;
import com.courseregistration.exception.EnrollmentCannotBeDroppedException;
import com.courseregistration.exception.EnrollmentNotFoundException;
import com.courseregistration.repository.CourseRepository;
import com.courseregistration.repository.EnrollmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        ReflectionTestUtils.setField(course, "id", 1L);
        course.setCourseName("Java Backend Development");
        course.setCapacity(2);
    }

    // Test 1: Enrollment when capacity is available
    @Test
    void shouldEnrollStudentWhenCapacityIsAvailable() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourse_Id(101L, 1L))
                .thenReturn(false);

        when(enrollmentRepository.countByCourse_IdAndStatus(
                1L, EnrollmentStatus.ENROLLED))
                .thenReturn(1L);

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = enrollmentService.enrollStudent(
                new EnrollmentRequest(101L, 1L, true));

        ArgumentCaptor<Enrollment> captor =
                ArgumentCaptor.forClass(Enrollment.class);

        verify(enrollmentRepository).save(captor.capture());

        assertNotNull(response);
        assertEquals(101L, captor.getValue().getStudentId());
        assertEquals(EnrollmentStatus.ENROLLED,
                captor.getValue().getStatus());
    }

    // Test 2: Waitlist when course is full
    @Test
    void shouldAddStudentToWaitlistWhenCourseIsFull() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourse_Id(103L, 1L))
                .thenReturn(false);

        when(enrollmentRepository.countByCourse_IdAndStatus(
                1L, EnrollmentStatus.ENROLLED))
                .thenReturn(2L);

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = enrollmentService.enrollStudent(
                new EnrollmentRequest(103L, 1L, true));

        ArgumentCaptor<Enrollment> captor =
                ArgumentCaptor.forClass(Enrollment.class);

        verify(enrollmentRepository).save(captor.capture());

        assertNotNull(response);
        assertEquals(EnrollmentStatus.WAITLISTED,
                captor.getValue().getStatus());
    }

    // Test 3: Duplicate enrollment prevention
    @Test
    void shouldRejectDuplicateEnrollment() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourse_Id(101L, 1L))
                .thenReturn(true);

        assertThrows(DuplicateEnrollmentException.class,
                () -> enrollmentService.enrollStudent(
                        new EnrollmentRequest(101L, 1L, true)));

        verify(enrollmentRepository, never()).save(any());
    }

    // Test 4: Prerequisite failure
    @Test
    void shouldMarkStudentIneligibleWhenPrerequisiteIsNotCompleted() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourse_Id(104L, 1L))
                .thenReturn(false);

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = enrollmentService.enrollStudent(
                new EnrollmentRequest(104L, 1L, false));

        ArgumentCaptor<Enrollment> captor =
                ArgumentCaptor.forClass(Enrollment.class);

        verify(enrollmentRepository).save(captor.capture());

        assertNotNull(response);
        assertEquals(EnrollmentStatus.INELIGIBLE,
                captor.getValue().getStatus());

        assertFalse(captor.getValue().isPrerequisiteCompleted());

        verify(enrollmentRepository, never())
                .countByCourse_IdAndStatus(anyLong(), any());
    }

    // Test 5: Invalid course ID
    @Test
    void shouldThrowExceptionWhenCourseDoesNotExist() {

        when(courseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class,
                () -> enrollmentService.enrollStudent(
                        new EnrollmentRequest(101L, 999L, true)));

        verify(enrollmentRepository, never()).save(any());
    }

    // Test 6: FIFO promotion
    @Test
    void shouldPromoteEarliestEligibleWaitlistedStudent() {

        Enrollment enrolled = createEnrollment(
                101L, EnrollmentStatus.ENROLLED, true);

        Enrollment waitlisted = createEnrollment(
                103L, EnrollmentStatus.WAITLISTED, true);

        when(enrollmentRepository.findById(1L))
                .thenReturn(Optional.of(enrolled));

        when(enrollmentRepository
                .findByCourse_IdAndStatusOrderByCreatedAtAsc(
                        1L, EnrollmentStatus.WAITLISTED))
                .thenReturn(List.of(waitlisted));

        enrollmentService.dropEnrollment(1L);

        assertEquals(EnrollmentStatus.DROPPED,
                enrolled.getStatus());

        assertEquals(EnrollmentStatus.ENROLLED,
                waitlisted.getStatus());

        verify(enrollmentRepository, times(2))
                .save(any(Enrollment.class));
    }

    // Test 7: Skip ineligible student and promote next eligible student
    @Test
    void shouldSkipIneligibleStudentAndPromoteNextEligibleStudent() {

        Enrollment enrolled = createEnrollment(
                101L, EnrollmentStatus.ENROLLED, true);

        Enrollment ineligible = createEnrollment(
                103L, EnrollmentStatus.WAITLISTED, false);

        Enrollment eligible = createEnrollment(
                105L, EnrollmentStatus.WAITLISTED, true);

        when(enrollmentRepository.findById(1L))
                .thenReturn(Optional.of(enrolled));

        when(enrollmentRepository
                .findByCourse_IdAndStatusOrderByCreatedAtAsc(
                        1L, EnrollmentStatus.WAITLISTED))
                .thenReturn(List.of(ineligible, eligible));

        enrollmentService.dropEnrollment(1L);

        assertEquals(EnrollmentStatus.DROPPED,
                enrolled.getStatus());

        assertEquals(EnrollmentStatus.INELIGIBLE,
                ineligible.getStatus());

        assertEquals(EnrollmentStatus.ENROLLED,
                eligible.getStatus());
    }

    // Test 8: Cannot drop a waitlisted student
    @Test
    void shouldRejectDropWhenEnrollmentIsNotEnrolled() {

        Enrollment waitlisted = createEnrollment(
                103L, EnrollmentStatus.WAITLISTED, true);

        when(enrollmentRepository.findById(3L))
                .thenReturn(Optional.of(waitlisted));

        assertThrows(EnrollmentCannotBeDroppedException.class,
                () -> enrollmentService.dropEnrollment(3L));

        verify(enrollmentRepository, never()).save(any());
    }

    // Test 9: Cannot drop an already dropped enrollment
    @Test
    void shouldRejectRepeatedDrop() {

        Enrollment dropped = createEnrollment(
                101L, EnrollmentStatus.DROPPED, true);

        when(enrollmentRepository.findById(1L))
                .thenReturn(Optional.of(dropped));

        assertThrows(EnrollmentCannotBeDroppedException.class,
                () -> enrollmentService.dropEnrollment(1L));

        verify(enrollmentRepository, never()).save(any());
    }

    // Test 10: Invalid enrollment ID
    @Test
    void shouldThrowExceptionWhenEnrollmentDoesNotExist() {

        when(enrollmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(EnrollmentNotFoundException.class,
                () -> enrollmentService.dropEnrollment(999L));
    }

    private Enrollment createEnrollment(
            Long studentId,
            EnrollmentStatus status,
            boolean prerequisiteCompleted) {

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourse(course);
        enrollment.setStatus(status);
        enrollment.setPrerequisiteCompleted(prerequisiteCompleted);

        return enrollment;
    }

    @Test
    void shouldReturnAllEnrollments() {
        Enrollment enrollment1 =
                createEnrollment(101L, EnrollmentStatus.ENROLLED, true);

        Enrollment enrollment2 =
                createEnrollment(102L, EnrollmentStatus.WAITLISTED, true);

        when(enrollmentRepository.findAll())
                .thenReturn(List.of(enrollment1, enrollment2));

        List<EnrollmentResponse> response =
                enrollmentService.getAllEnrollments();

        assertNotNull(response);
        assertEquals(2, response.size());

        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEnrollmentWhenIdExists() {
        Enrollment enrollment =
                createEnrollment(101L, EnrollmentStatus.ENROLLED, true);

        when(enrollmentRepository.findById(1L))
                .thenReturn(Optional.of(enrollment));

        EnrollmentResponse response =
                enrollmentService.getEnrollmentById(1L);

        assertNotNull(response);

        verify(enrollmentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingEnrollmentWithInvalidId() {
        when(enrollmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> enrollmentService.getEnrollmentById(999L)
        );

        verify(enrollmentRepository, times(1)).findById(999L);
    }
}
