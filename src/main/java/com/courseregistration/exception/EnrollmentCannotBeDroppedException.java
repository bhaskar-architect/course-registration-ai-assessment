package com.courseregistration.exception;

public class EnrollmentCannotBeDroppedException extends RuntimeException {

    public EnrollmentCannotBeDroppedException(String message) {
        super(message);
    }
}