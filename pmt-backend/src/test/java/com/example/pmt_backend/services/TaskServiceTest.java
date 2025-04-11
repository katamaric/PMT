package com.example.pmt_backend.services;

import com.example.pmt_backend.dao.ProjectRepository;
import com.example.pmt_backend.dao.TaskHistoryRepository;
import com.example.pmt_backend.dao.TaskRepository;
import com.example.pmt_backend.dao.UserRepository;
import com.example.pmt_backend.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskHistoryRepository taskHistoryRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TaskService taskService;

    private Project project;
    private User user;
    private User assignedUser;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setId(1L);
        project.setMembers(new ArrayList<>());  // Initialize the members list

        user = new User();
        user.setId(1L);
        user.setUsername("assigningUser");

        assignedUser = new User();
        assignedUser.setId(2L);
        assignedUser.setUsername("assignedUser");

        // Add both users to the project members list
        project.getMembers().add(user);
        project.getMembers().add(assignedUser);

        // Create a task and assign it to the assigned user
        task = new Task("Test Task", "Test Description", TaskStatus.TO_DO, 1, LocalDate.now(), project, assignedUser);
        task.setId(1L);  // Set a task ID
    }

    @Test
    void testCreateTask() {

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task, 1L);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getName());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTaskThrowsExceptionWhenProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task, 1L));
    }

    @Test
    void testUpdateTask() {
        // Mock the user who is updating the task
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user)); // Mock the user

        // Mock the task repository to return the correct task
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task)); // task with ID 1

        // Mock saving the updated task
        Task updatedTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDueDate(), task.getProject(), task.getAssignedTo());
        updatedTask.setId(task.getId());  // Ensure the updated task has the same ID

        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Call the method under test
        Task result = taskService.updateTask(task.getId(), updatedTask, user.getId());

        // Assert that the task is updated correctly
        assertEquals(updatedTask.getName(), result.getName());
        assertEquals(updatedTask.getDescription(), result.getDescription());
        assertEquals(updatedTask.getStatus(), result.getStatus());
        assertEquals(updatedTask.getPriority(), result.getPriority());
        assertEquals(updatedTask.getDueDate(), result.getDueDate());

        // Verify the task repository was called to find the task and save the updated task
        verify(taskRepository, times(1)).findById(task.getId());
        verify(taskRepository, times(1)).save(any(Task.class));

        // Verify the user repository was called to find the user
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testAssignTaskToUser() {
        // Mock the necessary repositories
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));  // task with ID 1
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));  // Mock assigning user
        when(userRepository.findById(assignedUser.getId())).thenReturn(Optional.of(assignedUser));  // Mock assigned user
        
        // Mock the task assignment to the user
        when(taskRepository.save(any(Task.class))).thenReturn(task);  // Simulate task save

        // Call the method under test
        Task result = taskService.assignTaskToUser(task.getId(), assignedUser.getId(), user.getId());

        // Assert that the result is not null
        assertNotNull(result, "Assigned task should not be null");

        // Verify that the task was assigned properly
        assertEquals(assignedUser.getId(), result.getAssignedTo().getId(), "Assigned user should match the expected user");

        // Verify the repositories were called
        verify(taskRepository, times(1)).findById(task.getId());
        verify(userRepository, times(2)).findById(anyLong());  // userRepository should be called twice: for assigningUser and assignedUser
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        taskService.deleteTask(1L, 1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTaskThrowsExceptionWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(1L, 1L));
    }

    @Test
    void testUpdateTaskStatus() {
        // Mock the user being passed to the service (user who is updating the task status)
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Mock the task repository to return the correct task
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task)); // task with ID 1

        // Mock the status change
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        Task updatedTask = new Task(task.getName(), task.getDescription(), newStatus, task.getPriority(), task.getDueDate(), task.getProject(), task.getAssignedTo());
        updatedTask.setId(task.getId());

        // Mock saving the updated task
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task result = taskService.updateTaskStatus(task.getId(), newStatus, user.getId());

        // Assert that the task status was updated
        assertEquals(newStatus, result.getStatus());

        // Verify the task repository was called to find the task and save the updated task
        verify(taskRepository, times(1)).findById(task.getId());
        verify(taskRepository, times(1)).save(any(Task.class));

        // Verify the user repository was called to find the user who is updating the task
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testGenerateChangeLog() {

        Task oldTask = new Task("Old Task", "Old Description", TaskStatus.TO_DO, 1, LocalDate.now(), project, assignedUser);
        Task newTask = new Task("New Task", "New Description", TaskStatus.IN_PROGRESS, 2, LocalDate.now(), project, assignedUser);

        String changeLog = taskService.generateChangeLog(oldTask, newTask);

        assertTrue(changeLog.contains("Name changed from 'Old Task' to 'New Task'"));
        assertTrue(changeLog.contains("Description changed."));
    }
}
