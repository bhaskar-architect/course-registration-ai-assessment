package com.courseregistration.repository;

import com.courseregistration.entity.Enrollment;
import com.courseregistration.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    long countByCourse_IdAndStatus(Long courseId, EnrollmentStatus status);

    boolean existsByStudentIdAndCourse_Id(Long studentId, Long courseId);

    List<Enrollment> findByCourse_IdAndStatusOrderByCreatedAtAsc(Long courseId, EnrollmentStatus status);

}