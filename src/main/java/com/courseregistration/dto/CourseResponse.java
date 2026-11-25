package com.courseregistration.dto;

public class CourseResponse {

    private Long id;
    private String courseName;
    private Integer capacity;

    public CourseResponse(Long id, String courseName, Integer capacity) {
        this.id = id;
        this.courseName = courseName;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public Integer getCapacity() {
        return capacity;
    }
}