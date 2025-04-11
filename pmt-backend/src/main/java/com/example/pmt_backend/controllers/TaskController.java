package com.example.pmt_backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pmt_backend.dto.TaskDTO;
import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.Task;
import com.example.pmt_backend.models.TaskHistory;
import com.example.pmt_backend.models.TaskStatus;
import com.example.pmt_backend.models.User;
import com.example.pmt_backend.services.ProjectService;
import com.example.pmt_backend.services.TaskService;
import com.example.pmt_backend.services.UserService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;  

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        User user = userOpt.get();
        List<Task> allTasks = taskService.getAllTasks();

        // Filter tasks based on project membership
        List<Task> filteredTasks = allTasks.stream()
            .filter(task -> task.getProject().getMembers().contains(user))
            .toList();

        return ResponseEntity.ok(filteredTasks);
    }
    
    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> getTaskHistory(@PathVariable Long id) {
        List<TaskHistory> historyList = taskService.getTaskHistory(id);
        return ResponseEntity.ok(historyList);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId, @RequestParam Long userId) {
        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<User> userOpt = userService.getUserById(userId);

        if (projectOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Project project = projectOpt.get();
        User user = userOpt.get();

        // Ensure the user is part of the project
        if (!project.getMembers().contains(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<Task> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Long userId) {
        return taskService.getTasksByUser(userId);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status, @RequestParam Long userId) {
    	Optional<User> userOpt = userService.getUserById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        User user = userOpt.get();
        List<Task> tasks = taskService.getTasksByStatus(status);

        // Filter tasks by projects the user is a member of
        List<Task> filteredTasks = tasks.stream()
            .filter(task -> task.getProject().getMembers().contains(user))
            .toList();

        return ResponseEntity.ok(filteredTasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, @RequestParam Long userId) {
        Optional<Task> taskOpt = taskService.getTaskById(id);
        Optional<User> userOpt = userService.getUserById(userId);

        if (taskOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Task task = taskOpt.get();
        User user = userOpt.get();

        // Ensure the user is part of the project
        if (!task.getProject().getMembers().contains(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return ResponseEntity.ok(task);
    }

    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO taskDTO, @RequestParam Long projectId, @RequestParam Long userId) {
        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        Optional<User> userOpt = userService.getUserById(userId);

        if (projectOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Project project = projectOpt.get();
        User user = userOpt.get();

        // Check if the user is part of the project
        if (!project.getMembers().contains(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        // Observers cannot create tasks
        if (user.getRole() == Role.OBSERVER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Task createdTask = taskService.createTaskFromDTO(taskDTO, project, user);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask, @RequestParam Long userId) {
        try {
            Task updated = taskService.updateTask(id, updatedTask, userId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> request, @RequestParam Long userId) {
        String statusString = request.get("status");
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        User user = userOpt.get();
        
        if (user.getRole() == Role.OBSERVER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        // Check if the status is valid
        try {
            TaskStatus status = TaskStatus.valueOf(statusString.toUpperCase());
            Task updatedTask = taskService.updateTaskStatus(id, status, userId);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<Task> assignTask(@PathVariable Long id, @RequestBody Map<String, Long> requestBody, @RequestParam Long assigningUserId) {
        Long userId = requestBody.get("userId");

        // Ensure the task exists
        Optional<Task> taskOpt = taskService.getTaskById(id);
        Optional<User> userOpt = userService.getUserById(userId);
        Optional<User> assigningUserOpt = userService.getUserById(assigningUserId);

        if (taskOpt.isEmpty() || userOpt.isEmpty() || assigningUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Task task = taskOpt.get();
        User assignedUser = userOpt.get();
        User assigningUser = assigningUserOpt.get();

        // Check if the assigning user is part of the project
        if (!task.getProject().getMembers().contains(assigningUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Check if the assigned user is part of the project
        if (!task.getProject().getMembers().contains(assignedUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Check if the assigning user has permission (Admin or Member)
        if (assigningUser.getRole() == Role.OBSERVER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Task assignedTask = taskService.assignTaskToUser(id, userId, assigningUserId);
        return ResponseEntity.ok(assignedTask);
    }
    
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @RequestParam Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userOpt.get();
        
        if (user.getRole() == Role.OBSERVER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build(); 
    }

}