package com.courseregistration.controller;

import com.courseregistration.dto.EnrollmentRequest;
import com.courseregistration.dto.EnrollmentResponse;
import com.courseregistration.service.EnrollmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // Enroll a student
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollStudent(
            @RequestBody EnrollmentRequest request) {

        EnrollmentResponse response =
                enrollmentService.enrollStudent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all enrollments
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {

        List<EnrollmentResponse> enrollments =
                enrollmentService.getAllEnrollments();

        return ResponseEntity.ok(enrollments);
    }

    // Get enrollment by ID
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(
            @PathVariable Long enrollmentId) {

        EnrollmentResponse response =
                enrollmentService.getEnrollmentById(enrollmentId);

        return ResponseEntity.ok(response);
    }

    // Drop enrollment
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> dropEnrollment(
            @PathVariable Long enrollmentId) {

        enrollmentService.dropEnrollment(enrollmentId);

        return ResponseEntity.noContent().build();
    }
}