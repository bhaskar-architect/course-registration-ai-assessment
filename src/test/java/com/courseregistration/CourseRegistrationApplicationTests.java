
package com.courseregistration;

import com.courseregistration.dto.EnrollmentRequest;
import com.courseregistration.dto.EnrollmentResponse;
import com.courseregistration.entity.Course;
import com.courseregistration.enums.EnrollmentStatus;
import com.courseregistration.repository.CourseRepository;
import com.courseregistration.repository.EnrollmentRepository;
import com.courseregistration.service.EnrollmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CourseRegistrationApplicationTests {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EnrollmentService enrollmentService;

    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();

        course = new Course();
        course.setCourseName("Java Backend Development");
        course.setCapacity(2);

        course = courseRepository.save(course);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldPromoteEarliestEligibleWaitlistedStudentWhenEnrolledStudentDrops() {

        // Enroll first two students
        EnrollmentResponse student101 = enrollmentService.enrollStudent(
                new EnrollmentRequest(101L, course.getId(), true));

        EnrollmentResponse student102 = enrollmentService.enrollStudent(
                new EnrollmentRequest(102L, course.getId(), true));

        // Add two students to waitlist
        EnrollmentResponse student103 = enrollmentService.enrollStudent(
                new EnrollmentRequest(103L, course.getId(), true));

        EnrollmentResponse student104 = enrollmentService.enrollStudent(
                new EnrollmentRequest(104L, course.getId(), true));

        assertEquals(EnrollmentStatus.ENROLLED, student101.status());
        assertEquals(EnrollmentStatus.ENROLLED, student102.status());
        assertEquals(EnrollmentStatus.WAITLISTED, student103.status());
        assertEquals(EnrollmentStatus.WAITLISTED, student104.status());

        // Drop the first enrolled student
        enrollmentService.dropEnrollment(student101.id());

        // Verify persisted statuses
        assertEquals(
                EnrollmentStatus.DROPPED,
                enrollmentRepository.findById(student101.id()).orElseThrow().getStatus());

        assertEquals(
                EnrollmentStatus.ENROLLED,
                enrollmentRepository.findById(student103.id()).orElseThrow().getStatus());

        assertEquals(
                EnrollmentStatus.WAITLISTED,
                enrollmentRepository.findById(student104.id()).orElseThrow().getStatus());

        // Verify capacity is still respected
        assertEquals(
                2,
                enrollmentRepository.countByCourse_IdAndStatus(
                        course.getId(), EnrollmentStatus.ENROLLED));
    }
}
