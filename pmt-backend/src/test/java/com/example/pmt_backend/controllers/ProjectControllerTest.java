package com.example.pmt_backend.controllers;

import com.example.pmt_backend.dto.AddMemberRequest;
import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.User;
import com.example.pmt_backend.services.ProjectService;
import com.example.pmt_backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

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
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setName("Test Project");
        mockProject.setDescription("Test project description");
        mockProject.setStartDate(LocalDate.of(2025, 4, 6));

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("user1");
        mockUser.setEmail("user1@example.com");
    }

    @Test
    void testCreateProject() throws Exception {
        when(projectService.createProject(any(Project.class), eq(1L))).thenReturn(mockProject);

        String requestBody = "{ \"name\": \"Test Project\", \"description\": \"Test project description\", \"startDate\": \"2025-04-06\", \"adminId\": 1 }";

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Project"));

        verify(projectService, times(1)).createProject(any(Project.class), eq(1L));
    }

    @Test
    void testGetAllProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(mockProject));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void testGetProjectByIdFound() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(mockProject));

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"));

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
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProject(1L);
    }

    @Test
    void testGetProjectsByAdminId() throws Exception {
        when(projectService.getProjectsByAdminId(1L)).thenReturn(Arrays.asList(mockProject));

        mockMvc.perform(get("/api/projects/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService, times(1)).getProjectsByAdminId(1L);
    }

    @Test
    void testGetUserProjects() throws Exception {
        when(projectService.getProjectsByUser(1L)).thenReturn(Arrays.asList(mockProject));

        mockMvc.perform(get("/api/projects/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Project"));

        verify(projectService, times(1)).getProjectsByUser(1L);
    }

    @Test
    void testGetProjectMembersFound() throws Exception {
        mockProject.setMembers(Arrays.asList(mockUser));
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(mockProject));

        mockMvc.perform(get("/api/projects/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"));

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void testGetProjectMembersNotFound() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/1/members"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    void testInviteMemberToProjectSuccess() throws Exception {
        AddMemberRequest request = new AddMemberRequest();
        request.setEmail("user1@example.com");
        request.setRole(Role.MEMBER);

        when(userService.getUserByEmail("user1@example.com")).thenReturn(mockUser);
        when(projectService.addMemberToProjectWithRole(1L, mockUser, Role.MEMBER, 1L)).thenReturn(mockProject);

        String requestBody = "{ \"email\": \"user1@example.com\", \"role\": \"MEMBER\" }";

        mockMvc.perform(post("/api/projects/1/invite-member?adminId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserByEmail("user1@example.com");
        verify(projectService, times(1)).addMemberToProjectWithRole(1L, mockUser, Role.MEMBER, 1L);
    }

    @Test
    void testInviteMemberToProjectUserNotFound() throws Exception {
        when(userService.getUserByEmail("unknown@example.com")).thenReturn(null);

        String requestBody = "{ \"email\": \"unknown@example.com\", \"role\": \"MEMBER\" }";

        mockMvc.perform(post("/api/projects/1/invite-member?adminId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInviteMemberToProjectUnauthorized() throws Exception {
        when(userService.getUserByEmail("user1@example.com")).thenReturn(mockUser);
        when(projectService.addMemberToProjectWithRole(anyLong(), any(), any(), anyLong()))
                .thenThrow(new RuntimeException());

        String requestBody = "{ \"email\": \"user1@example.com\", \"role\": \"MEMBER\" }";

        mockMvc.perform(post("/api/projects/1/invite-member?adminId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}
