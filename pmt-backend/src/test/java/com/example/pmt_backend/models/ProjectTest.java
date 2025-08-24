package com.example.pmt_backend.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testGettersAndSetters() {
        Project project = new Project();

        User admin = new User();
        admin.setId(1L);
        admin.setUsername("adminUser");

        Task task1 = new Task();
        task1.setId(101L);

        RoleAssignment ra = new RoleAssignment();
        ra.setId(201L);

        User member = new User();
        member.setId(2L);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);

        List<User> members = new ArrayList<>();
        members.add(member);

        List<RoleAssignment> roleAssignments = new ArrayList<>();
        roleAssignments.add(ra);

        // Set fields
        project.setId(1L);
        project.setName("Project A");
        project.setDescription("Description A");
        project.setStartDate(LocalDate.of(2025, 8, 24));
        project.setAdmin(admin);
        project.setTasks(tasks);
        project.setMembers(members);

        // Assert getters
        assertEquals(1L, project.getId());
        assertEquals("Project A", project.getName());
        assertEquals("Description A", project.getDescription());
        assertEquals(LocalDate.of(2025, 8, 24), project.getStartDate());
        assertEquals(admin, project.getAdmin());
        assertEquals(tasks, project.getTasks());
        assertEquals(members, project.getMembers());
    }
}
