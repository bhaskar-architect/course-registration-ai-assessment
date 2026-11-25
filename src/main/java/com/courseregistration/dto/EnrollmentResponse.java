package com.courseregistration.dto;

import com.courseregistration.enums.EnrollmentStatus;
import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        Long courseId,
        String courseName,
        EnrollmentStatus status,
        LocalDateTime createdAt,
        boolean prerequisiteCompleted
) {
}