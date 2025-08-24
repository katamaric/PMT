package com.example.pmt_backend.controllers;

import com.example.pmt_backend.dto.TaskDTO;
import com.example.pmt_backend.models.*;
import com.example.pmt_backend.services.TaskService;
import com.example.pmt_backend.services.ProjectService;
import com.example.pmt_backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
	@MockBean
    private TaskService taskService;

    @SuppressWarnings("removal")
	@MockBean
    private ProjectService projectService;

    @SuppressWarnings("removal")
	@MockBean
    private UserService userService;

    @SuppressWarnings("removal")
	@MockBean
    private Task task;

    @SuppressWarnings("removal")
	@MockBean
    private User user;

    @SuppressWarnings("removal")
	@MockBean
    private Project project;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole(Role.MEMBER);

        project = new Project();
        project.setId(1L);
        project.setName("Project A");
        project.setMembers(Collections.singletonList(user));

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setProject(project);
        task.setStatus(TaskStatus.TO_DO);
        task.setAssignedTo(user);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("New Task"); // Name as expected in the test
        taskDTO.setDescription("Task description");
        taskDTO.setStatus(TaskStatus.TO_DO);

        Long projectId = 1L;
        Long userId = 1L;

        Task task = new Task();
        task.setName("New Task");
        task.setDescription("Task description");
        task.setStatus(TaskStatus.TO_DO);
        task.setProject(new Project());
        task.setAssignedTo(new User());

        // Mock service calls
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(taskService.createTaskFromDTO(any(TaskDTO.class), eq(project), eq(user))).thenReturn(task); // Ensure this returns the "New Task"

        mockMvc.perform(post("/api/tasks")
                .param("projectId", String.valueOf(projectId))
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Task")) // Ensure the name matches what was set in the DTO and mock
                .andExpect(jsonPath("$.status").value("TO_DO"));
    }


    @Test
    void testGetAllTasks() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        // Create the project and initialize members list as empty, then add user
        Project project = new Project();
        project.setMembers(new ArrayList<>());
        project.getMembers().add(user); // Ensure the user is part of the project

        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("Task 1");
        task1.setDescription("Task description");
        task1.setStatus(TaskStatus.TO_DO);
        task1.setProject(project);
        task1.setAssignedTo(user);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("Task 2");
        task2.setDescription("Task description");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setProject(project);
        task2.setAssignedTo(user);

        List<Task> tasks = List.of(task1, task2);

        // Mock the user service and task service behavior
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks")
                .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // Expecting 2 tasks
    }



    @Test
    void testUpdateTaskStatus() throws Exception {
        Long taskId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setRole(Role.MEMBER);

        Task task = new Task();
        task.setId(taskId);
        task.setName("Task with status");
        task.setProject(new Project());
        task.setStatus(TaskStatus.TO_DO); // Initial status TO_DO
        task.setAssignedTo(user);

        // Mock behavior: update status to IN_PROGRESS
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setName("Task with status");
        updatedTask.setProject(new Project());
        updatedTask.setStatus(TaskStatus.IN_PROGRESS); // Status after update
        updatedTask.setAssignedTo(user);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(taskService.updateTaskStatus(taskId, TaskStatus.IN_PROGRESS, userId)).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                .param("userId", String.valueOf(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS")); // Ensure the updated status is IN_PROGRESS
    }
    
    @Test
    void testGetAllTasksWithInvalidUserId() throws Exception {
        Long invalidUserId = 999L;

        mockMvc.perform(get("/api/tasks")
                .param("userId", String.valueOf(invalidUserId)))
                .andExpect(status().isBadRequest()); // Should return 400
    }
    
    @Test
    void testGetTaskHistoryWithInvalidTaskId() throws Exception {
        Long invalidTaskId = 999L; // Task ID does not exist in the mock data

        mockMvc.perform(get("/api/tasks/{id}/history", invalidTaskId))
                .andExpect(status().isOk())  // Should return 200, with an empty list
                .andExpect(jsonPath("$").isEmpty()); // Empty task history
    }

    @Test
    void testGetTasksByProjectWithInvalidProjectOrUserId() throws Exception {
        Long invalidProjectId = 999L; // Project does not exist
        Long invalidUserId = 999L; // User does not exist

        mockMvc.perform(get("/api/tasks/project/{projectId}", invalidProjectId)
                .param("userId", String.valueOf(invalidUserId)))
                .andExpect(status().isBadRequest()); // Should return 400
    }
    
    @Test
    void createTask_ShouldReturnForbiddenIfObserver() throws Exception {
        user.setRole(Role.OBSERVER);
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(project));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Task");
        
        mockMvc.perform(post("/api/tasks")
                .param("projectId", "1")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTask_ShouldReturnForbiddenIfUserNotMember() throws Exception {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setRole(Role.MEMBER);
        when(projectService.getProjectById(1L)).thenReturn(Optional.of(project));
        when(userService.getUserById(2L)).thenReturn(Optional.of(anotherUser));

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setName("Task");

        mockMvc.perform(post("/api/tasks")
                .param("projectId", "1")
                .param("userId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void updateTaskStatus_ShouldReturnForbiddenIfObserver() throws Exception {
        user.setRole(Role.OBSERVER);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/api/tasks/1/status")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTaskStatus_ShouldReturnBadRequestForInvalidStatus() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/api/tasks/1/status")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID_STATUS\"}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void assignTask_ShouldReturnForbiddenIfObserverAssigning() throws Exception {
        user.setRole(Role.OBSERVER);
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/api/tasks/1/assign")
                .param("assigningUserId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void assignTask_ShouldReturnForbiddenIfAssignedUserNotMember() throws Exception {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setRole(Role.MEMBER);
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(Optional.of(anotherUser));
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/api/tasks/1/assign")
                .param("assigningUserId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":2}"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void deleteTask_ShouldReturnForbiddenIfObserver() throws Exception {
        user.setRole(Role.OBSERVER);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/tasks/1")
                .param("userId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTask_ShouldReturnBadRequestIfUserNotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/tasks/1")
                .param("userId", "999"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void updateTask_ShouldReturnForbiddenForIllegalArgument() throws Exception {
        when(taskService.updateTask(eq(1L), any(Task.class), eq(1L)))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/api/tasks/1")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Task\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTask_ShouldReturnBadRequestForRuntimeException() throws Exception {
        when(taskService.updateTask(eq(1L), any(Task.class), eq(1L)))
                .thenThrow(new RuntimeException());

        mockMvc.perform(put("/api/tasks/1")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Task\"}"))
                .andExpect(status().isBadRequest());
    }
}
