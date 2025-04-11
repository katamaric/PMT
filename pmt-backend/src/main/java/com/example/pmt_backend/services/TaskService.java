package com.example.pmt_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pmt_backend.dao.ProjectRepository;
import com.example.pmt_backend.dao.TaskHistoryRepository;
import com.example.pmt_backend.dao.TaskRepository;
import com.example.pmt_backend.dao.UserRepository;
import com.example.pmt_backend.dto.TaskDTO;
import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.Task;
import com.example.pmt_backend.models.TaskHistory;
import com.example.pmt_backend.models.TaskStatus;
import com.example.pmt_backend.models.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class TaskService {
	
	@PersistenceContext
	private EntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskHistoryRepository taskHistoryRepository;
    
    @Autowired
    private EmailService emailService;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedToId(userId);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    @Transactional
    public Task createTask(Task task, Long userId) {
        Project project = projectRepository.findById(task.getProject().getId())
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure the user creating the task is part of the project
        if (!project.getMembers().contains(user)) {
            throw new IllegalArgumentException("User is not a member of this project");
        }

        // Ensure the assigned user is also part of the project
        if (task.getAssignedTo() != null && !project.getMembers().contains(task.getAssignedTo())) {
            throw new IllegalArgumentException("Assigned user is not a member of this project");
        }

        task.setProject(project);
        return taskRepository.save(task);
    }

    @Transactional
    public Task createTaskFromDTO(TaskDTO taskDTO, Project project, User user) {

        // Ensure the assigned user is a project member
        User assignedUser = userRepository.findById(taskDTO.getAssignedTo())
            .orElseThrow(() -> new IllegalArgumentException("Assigned user not found"));
        
        if (!project.getMembers().contains(assignedUser)) {
            throw new IllegalArgumentException("Assigned user is not a member of this project");
        }

        Task task = new Task();
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setDueDate(taskDTO.getDueDate());
        task.setProject(project);
        task.setAssignedTo(assignedUser);

        Task createdTask = taskRepository.save(task);

        // Trigger email notification for task assignment
        emailService.sendTaskAssignmentNotification(assignedUser, task);

        return createdTask;
    }
    
    @Transactional
    public Task updateTask(Long id, Task updatedTask, Long userId) {
        Task existingTask = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the user is part of the project and not an OBSERVER
        if (!existingTask.getProject().getMembers().contains(user)) {
            throw new IllegalArgumentException("User is not a member of the project");
        }
        if (user.getRole() == Role.OBSERVER) {
            throw new IllegalArgumentException("Observers cannot update tasks");
        }

        Task oldTask = new Task();
        oldTask.setName(existingTask.getName());
        oldTask.setDescription(existingTask.getDescription());
        oldTask.setStatus(existingTask.getStatus());
        oldTask.setPriority(existingTask.getPriority());
        oldTask.setDueDate(existingTask.getDueDate());
        oldTask.setEndDate(existingTask.getEndDate());
        oldTask.setAssignedTo(existingTask.getAssignedTo());

        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setEndDate(updatedTask.getEndDate());

        Task savedTask = taskRepository.save(existingTask);

        String changes = generateChangeLog(oldTask, existingTask);
        if (!changes.isEmpty()) {
            TaskHistory history = new TaskHistory(existingTask, user, changes);
            taskHistoryRepository.save(history);
        }

        return savedTask;
    }

    
    public String generateChangeLog(Task oldTask, Task newTask) {
        StringBuilder log = new StringBuilder();
        
        if (!oldTask.getName().equals(newTask.getName())) {
            log.append(String.format("Name changed from '%s' to '%s'. ", oldTask.getName(), newTask.getName()));
        }
        if (!oldTask.getDescription().equals(newTask.getDescription())) {
            log.append(String.format("Description changed. "));
        }
        if (!oldTask.getStatus().equals(newTask.getStatus())) {
            log.append(String.format("Status changed from '%s' to '%s'. ", oldTask.getStatus(), newTask.getStatus()));
        }
        if ((oldTask.getPriority() != null && !oldTask.getPriority().equals(newTask.getPriority())) ||
            (oldTask.getPriority() == null && newTask.getPriority() != null)) {
            log.append(String.format("Priority changed from '%s' to '%s'. ", oldTask.getPriority(), newTask.getPriority()));
        }
        if ((oldTask.getDueDate() != null && !oldTask.getDueDate().equals(newTask.getDueDate())) ||
            (oldTask.getDueDate() == null && newTask.getDueDate() != null)) {
            log.append(String.format("Due date changed from '%s' to '%s'. ", oldTask.getDueDate(), newTask.getDueDate()));
        }
        if ((oldTask.getEndDate() != null && !oldTask.getEndDate().equals(newTask.getEndDate())) ||
            (oldTask.getEndDate() == null && newTask.getEndDate() != null)) {
            log.append(String.format("End date changed from '%s' to '%s'. ", oldTask.getEndDate(), newTask.getEndDate()));
        }
        if (oldTask.getAssignedTo() != null && newTask.getAssignedTo() != null &&
            !oldTask.getAssignedTo().getId().equals(newTask.getAssignedTo().getId())) {
            log.append(String.format("Assigned user changed from '%s' to '%s'. ",
                oldTask.getAssignedTo().getUsername(), newTask.getAssignedTo().getUsername()));
        }

        return log.toString().trim();
    }

    @Transactional
    public Task updateTaskStatus(Long taskId, TaskStatus status, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure the user updating the task status is part of the project**
        if (!task.getProject().getMembers().contains(user)) {
            throw new IllegalArgumentException("User is not authorized to update this task status");
        }
        
        TaskStatus oldStatus = task.getStatus(); // Track old status

        task.setStatus(status);
        Task updated = taskRepository.save(task);

        // Save to history if status changed
        if (!oldStatus.equals(status)) {
            String change = String.format("Status changed from '%s' to '%s'.", oldStatus, status);
            TaskHistory history = new TaskHistory(task, user, change);
            taskHistoryRepository.save(history);
        }

        return updated;
    }

    @Transactional
    public Task assignTaskToUser(Long taskId, Long userId, Long assigningUserId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        User assigningUser = userRepository.findById(assigningUserId)
            .orElseThrow(() -> new IllegalArgumentException("Assigning user not found"));

        User assignedUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Assigned user not found"));

        // Ensure the assigning user has permission (Admin or Member)
        if (!task.getProject().getMembers().contains(assigningUser)) {
            throw new IllegalArgumentException("User is not authorized to assign this task");
        }

        // Ensure the assigned user is a member of the project
        if (!task.getProject().getMembers().contains(assignedUser)) {
            throw new IllegalArgumentException("Assigned user is not part of this project");
        }
        
        String previousUser = task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : "none";
        String changes = String.format("Assigned user changed from '%s' to '%s'.", previousUser, assignedUser.getUsername());
        
        task.setAssignedTo(assignedUser);
        Task updatedTask = taskRepository.save(task);
        
        // Save task history
        TaskHistory history = new TaskHistory(task, assigningUser, changes);
        taskHistoryRepository.save(history);

        // Send notification email
        emailService.sendTaskAssignmentNotification(assignedUser, task);

        return updatedTask;
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure only a project member (not Observer) can delete a task
        if (!task.getProject().getMembers().contains(user)) {
            throw new IllegalArgumentException("User is not authorized to delete this task");
        }

        // Ensure the user is not an Observer
        if (user.getRole() == Role.OBSERVER) {
            throw new IllegalArgumentException("Observers are not allowed to delete tasks");
        }

        taskRepository.deleteById(id);
    }

    public List<TaskHistory> getTaskHistory(Long taskId) {
        return taskHistoryRepository.findByTaskId(taskId);
    }

    
}
