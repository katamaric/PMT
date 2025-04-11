package com.example.pmt_backend.controllers;

import com.example.pmt_backend.dto.ProjectRequest;
import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.services.ProjectService;
import com.example.pmt_backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private ProjectService projectService;

    @SuppressWarnings("removal")
	@MockBean
    private UserService userService;

    @InjectMocks
    private ProjectController projectController;

    private Project mockProject;
    private ProjectRequest projectRequest;

    @BeforeEach
    void setUp() {
        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setName("Test Project");
        mockProject.setDescription("Test project description");
        mockProject.setStartDate(LocalDate.of(2025, 4, 6));

        projectRequest = new ProjectRequest();
        projectRequest.setName("Test Project");
        projectRequest.setDescription("Test project description");
        projectRequest.setStartDate(LocalDate.of(2025, 4, 6));
        projectRequest.setAdminId(1L);
    }

    @Test
    void testCreateProject() throws Exception {
        // Mock service method
        when(projectService.createProject(any(Project.class), eq(1L))).thenReturn(mockProject);

        // Perform the request
        mockMvc.perform(post("/api/projects")
                .contentType("application/json")
                .content("{ \"name\": \"Test Project\", \"description\": \"Test project description\", \"startDate\": \"2025-04-06\", \"adminId\": 1 }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.startDate").value("2025-04-06"));

        // Verify that the service method was called
        verify(projectService, times(1)).createProject(any(Project.class), eq(1L));
    }

    @Test
    void testGetAllProjects() throws Exception {
    	
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(mockProject));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"))
                .andExpect(jsonPath("$[0].startDate").value("2025-04-06"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetProjectById() throws Exception {
 
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(mockProject));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.startDate").value("2025-04-06"));

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void testGetProjectByIdNotFound() throws Exception {

        when(projectService.getProjectById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void testDeleteProject() throws Exception {

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProject(1L);
    }
}
