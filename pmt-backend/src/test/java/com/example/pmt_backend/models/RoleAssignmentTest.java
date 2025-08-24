package com.example.pmt_backend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleAssignmentTest {

    @Test
    void testGettersAndSetters() {
        RoleAssignment ra = new RoleAssignment();
        
        User user = new User();
        Project project = new Project();
        Role role = Role.MEMBER;
        
        ra.setId(1L);
        ra.setUser(user);
        ra.setProject(project);
        ra.setRoleName(role);

        assertEquals(1L, ra.getId());
        assertEquals(user, ra.getUser());
        assertEquals(project, ra.getProject());
        assertEquals(role, ra.getRoleName());
    }
}
