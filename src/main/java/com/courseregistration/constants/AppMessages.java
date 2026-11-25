package com.courseregistration.constants;

public final class AppMessages {

    private AppMessages() {
        // Prevent object creation
    }

    // Course messages
    public static final String COURSE_NOT_FOUND =
            "Course not found with id: %s";

    // Enrollment messages
    public static final String ENROLLMENT_NOT_FOUND =
            "Enrollment not found with id: %s";

    public static final String DUPLICATE_ENROLLMENT =
            "Student is already registered for this course";

    public static final String ENROLLMENT_CANNOT_BE_DROPPED =
            "Only enrolled students can drop the course";

    public static final String COURSE_NAME_ALREADY_EXISTS =
            "Course name %s already exists";

}