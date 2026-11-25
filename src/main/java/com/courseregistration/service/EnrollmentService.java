package com.courseregistration.service;

import com.courseregistration.dto.EnrollmentRequest;
import com.courseregistration.dto.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enrollStudent(EnrollmentRequest enrollmentRequest);

    void dropEnrollment(Long enrollmentId);

    List<EnrollmentResponse> getAllEnrollments();

    EnrollmentResponse getEnrollmentById(Long enrollmentId);
}