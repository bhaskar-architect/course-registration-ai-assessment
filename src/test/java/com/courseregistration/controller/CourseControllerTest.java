
package com.courseregistration.controller;

import com.courseregistration.dto.CourseRequest;
import com.courseregistration.dto.CourseResponse;
import com.courseregistration.service.CourseService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @Test
    void shouldCreateCourse() throws Exception {

        when(courseService.saveCourse(any(CourseRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseName": "Java Backend Development",
                                  "capacity": 2
                                }
                                """))
                .andExpect(status().isCreated());

        verify(courseService, times(1))
                .saveCourse(any(CourseRequest.class));
    }

    @Test
    void shouldGetAllCourses() throws Exception {

        when(courseService.getAllCourses())
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void shouldUpdateCourse() throws Exception {

        when(courseService.updateCourse(
                eq(1L), any(CourseRequest.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseName": "Java Advanced",
                                  "capacity": 3
                                }
                                """))
                .andExpect(status().isOk());

        verify(courseService, times(1))
                .updateCourse(eq(1L), any(CourseRequest.class));
    }

    @Test
    void shouldDeleteCourse() throws Exception {

        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).deleteCourse(1L);
    }
}
