
package com.courseregistration.controller;

import com.courseregistration.dto.EnrollmentRequest;
import com.courseregistration.service.EnrollmentService;

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

import java.util.List;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Test
    void shouldEnrollStudent() throws Exception {

        when(enrollmentService.enrollStudent(
                any(EnrollmentRequest.class)))
                .thenReturn(null);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 101,
                                  "courseId": 1,
                                  "prerequisiteCompleted": true
                                }
                                """))
                .andExpect(status().isCreated());

        verify(enrollmentService, times(1))
                .enrollStudent(any(EnrollmentRequest.class));
    }

    @Test
    void shouldGetAllEnrollments() throws Exception {

        when(enrollmentService.getAllEnrollments())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/enrollments"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(enrollmentService, times(1))
                .getAllEnrollments();
    }

    @Test
    void shouldGetEnrollmentById() throws Exception {

        when(enrollmentService.getEnrollmentById(1L))
                .thenReturn(null);

        mockMvc.perform(get("/api/enrollments/1"))
                .andExpect(status().isOk());

        verify(enrollmentService, times(1))
                .getEnrollmentById(1L);
    }

    @Test
    void shouldDropEnrollment() throws Exception {

        doNothing().when(enrollmentService)
                .dropEnrollment(1L);

        mockMvc.perform(delete("/api/enrollments/1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1))
                .dropEnrollment(1L);
    }
}
