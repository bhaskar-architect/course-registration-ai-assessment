package com.courseregistration.dto;

public record EnrollmentRequest(
        Long studentId,
        Long courseId,
        boolean prerequisiteCompleted
) {
}