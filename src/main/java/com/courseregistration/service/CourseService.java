package com.courseregistration.service;

import com.courseregistration.dto.CourseRequest;
import com.courseregistration.dto.CourseResponse;

import java.util.List;

public interface CourseService {

    List<CourseResponse> getAllCourses();

    CourseResponse saveCourse(CourseRequest request);

    CourseResponse updateCourse(Long id, CourseRequest request);

    void deleteCourse(Long id);
}