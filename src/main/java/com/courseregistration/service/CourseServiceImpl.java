package com.courseregistration.service;

import com.courseregistration.dto.CourseRequest;
import com.courseregistration.dto.CourseResponse;
import com.courseregistration.entity.Course;
import com.courseregistration.exception.CourseNotFoundException;
import com.courseregistration.exception.DuplicateCourseException;
import com.courseregistration.repository.CourseRepository;

import org.springframework.stereotype.Service;

import java.util.List;

import static com.courseregistration.constants.AppMessages.COURSE_NAME_ALREADY_EXISTS;
import static com.courseregistration.constants.AppMessages.COURSE_NOT_FOUND;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CourseResponse saveCourse(CourseRequest request) {

        if (courseRepository.existsByCourseNameIgnoreCase(request.getCourseName())) {
            throw new DuplicateCourseException(
                    String.format(COURSE_NAME_ALREADY_EXISTS, request.getCourseName()));
        }

        Course course = new Course();
        course.setCourseName(request.getCourseName());
        course.setCapacity(request.getCapacity());

        Course savedCourse = courseRepository.save(course);

        return mapToResponse(savedCourse);
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest request) {

        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(
                        String.format(COURSE_NOT_FOUND, id)));

        if (courseRepository.existsByCourseNameIgnoreCase(request.getCourseName())
                && !existingCourse.getCourseName()
                .equalsIgnoreCase(request.getCourseName())) {

            throw new DuplicateCourseException(
                    String.format(COURSE_NAME_ALREADY_EXISTS, request.getCourseName()));
        }

        existingCourse.setCourseName(request.getCourseName());
        existingCourse.setCapacity(request.getCapacity());

        Course updatedCourse = courseRepository.save(existingCourse);

        return mapToResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(
                        String.format(COURSE_NOT_FOUND, id)));

        courseRepository.delete(course);
    }

    private CourseResponse mapToResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCourseName(),
                course.getCapacity()
        );
    }
}