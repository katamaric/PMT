package com.example.pmt_backend.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();
        Project project = new Project();
        
        Task task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Task description");
        task.setStatus(TaskStatus.TO_DO);
        task.setPriority(5);
        task.setDueDate(LocalDate.of(2025, 8, 30));
        task.setEndDate(LocalDate.of(2025, 9, 5));
        task.setProject(project);
        task.setAssignedTo(user);

        assertEquals(1L, task.getId());
        assertEquals("Test Task", task.getName());
        assertEquals("Task description", task.getDescription());
        assertEquals(TaskStatus.TO_DO, task.getStatus());
        assertEquals(5, task.getPriority());
        assertEquals(LocalDate.of(2025, 8, 30), task.getDueDate());
        assertEquals(LocalDate.of(2025, 9, 5), task.getEndDate());
        assertEquals(project, task.getProject());
        assertEquals(user, task.getAssignedTo());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        Project project = new Project();
        Task task = new Task(
                "Task Name",
                "Task Descript",
                TaskStatus.IN_PROGRESS,
                3,
                LocalDate.of(2025, 8, 30),
                project,
                user
        );

        assertEquals("Task Name", task.getName());
        assertEquals("Task Descript", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(3, task.getPriority());
        assertEquals(LocalDate.of(2025, 8, 30), task.getDueDate());
        assertEquals(project, task.getProject());
        assertEquals(user, task.getAssignedTo());
    }
}
