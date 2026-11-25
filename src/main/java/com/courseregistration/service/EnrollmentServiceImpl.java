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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.courseregistration.constants.AppMessages.COURSE_NOT_FOUND;
import static com.courseregistration.constants.AppMessages.DUPLICATE_ENROLLMENT;
import static com.courseregistration.constants.AppMessages.ENROLLMENT_CANNOT_BE_DROPPED;
import static com.courseregistration.constants.AppMessages.ENROLLMENT_NOT_FOUND;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CourseNotFoundException(
                        String.format(COURSE_NOT_FOUND, request.courseId())));

        if (enrollmentRepository.existsByStudentIdAndCourse_Id(
                request.studentId(), request.courseId())) {
            throw new DuplicateEnrollmentException(DUPLICATE_ENROLLMENT);
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setStudentId(request.studentId());
        enrollment.setCourse(course);
        enrollment.setPrerequisiteCompleted(request.prerequisiteCompleted());
        enrollment.setCreatedAt(LocalDateTime.now());

        if (!request.prerequisiteCompleted()) {
            enrollment.setStatus(EnrollmentStatus.INELIGIBLE);
        } else {

            long enrolledCount = enrollmentRepository.countByCourse_IdAndStatus(
                    request.courseId(), EnrollmentStatus.ENROLLED);

            if (enrolledCount < course.getCapacity()) {
                enrollment.setStatus(EnrollmentStatus.ENROLLED);
            } else {
                enrollment.setStatus(EnrollmentStatus.WAITLISTED);
            }
        }

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return mapToResponse(savedEnrollment);
    }

    @Transactional
    @Override
    public void dropEnrollment(Long enrollmentId) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(
                        String.format(ENROLLMENT_NOT_FOUND, enrollmentId)));

        if (enrollment.getStatus() != EnrollmentStatus.ENROLLED) {
            throw new EnrollmentCannotBeDroppedException(
                    ENROLLMENT_CANNOT_BE_DROPPED);
        }

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);

        List<Enrollment> waitlistedStudents =
                enrollmentRepository.findByCourse_IdAndStatusOrderByCreatedAtAsc(
                        enrollment.getCourse().getId(),
                        EnrollmentStatus.WAITLISTED);

        for (Enrollment student : waitlistedStudents) {

            if (student.isPrerequisiteCompleted()) {
                student.setStatus(EnrollmentStatus.ENROLLED);
                enrollmentRepository.save(student);
                break;
            } else {
                student.setStatus(EnrollmentStatus.INELIGIBLE);
                enrollmentRepository.save(student);
            }
        }
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {

        return enrollmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException(
                        String.format(ENROLLMENT_NOT_FOUND, enrollmentId)));

        return mapToResponse(enrollment);
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {

        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudentId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getCourseName(),
                enrollment.getStatus(),
                enrollment.getCreatedAt(),
                enrollment.isPrerequisiteCompleted()
        );
    }
}