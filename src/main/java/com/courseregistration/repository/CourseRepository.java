package com.courseregistration.repository;

import com.courseregistration.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCourseNameIgnoreCase(String courseName);
}
