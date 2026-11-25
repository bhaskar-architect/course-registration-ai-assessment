
package com.courseregistration.exception;

import com.courseregistration.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(
            CourseNotFoundException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEnrollmentNotFound(
            EnrollmentNotFoundException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DuplicateEnrollmentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEnrollment(
            DuplicateEnrollmentException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(EnrollmentCannotBeDroppedException.class)
    public ResponseEntity<ErrorResponse> handleEnrollmentCannotBeDropped(
            EnrollmentCannotBeDroppedException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex, HttpStatus.BAD_REQUEST, request);
    }

    // Handle unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    // Common error response builder
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                status == HttpStatus.INTERNAL_SERVER_ERROR
                        ? "An unexpected error occurred"
                        : ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    @ExceptionHandler(DuplicateCourseException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCourse(
            DuplicateCourseException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                ex, HttpStatus.CONFLICT, request);
    }
}
