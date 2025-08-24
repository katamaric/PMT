package com.example.pmt_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.pmt_backend.dao.RoleAssignmentRepository;
import com.example.pmt_backend.models.RoleAssignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

class RoleAssignmentServiceTest {

    @Mock
    private RoleAssignmentRepository roleAssignmentRepository;

    @InjectMocks
    private RoleAssignmentService roleAssignmentService;

    private RoleAssignment roleAssignment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        roleAssignment = new RoleAssignment();
        roleAssignment.setId(1L);
    }

    @Test
    void getAllRoles_ShouldReturnAllRoles() {
        when(roleAssignmentRepository.findAll()).thenReturn(List.of(roleAssignment));

        List<RoleAssignment> result = roleAssignmentService.getAllRoles();

        assertEquals(1, result.size());
        assertEquals(roleAssignment, result.get(0));
        verify(roleAssignmentRepository, times(1)).findAll();
    }

    @Test
    void getRolesByUserId_ShouldReturnRolesForUser() {
        when(roleAssignmentRepository.findByUserId(2L)).thenReturn(List.of(roleAssignment));

        List<RoleAssignment> result = roleAssignmentService.getRolesByUserId(2L);

        assertEquals(1, result.size());
        verify(roleAssignmentRepository, times(1)).findByUserId(2L);
    }

    @Test
    void getRolesByProjectId_ShouldReturnRolesForProject() {
        when(roleAssignmentRepository.findByProjectId(3L)).thenReturn(List.of(roleAssignment));

        List<RoleAssignment> result = roleAssignmentService.getRolesByProjectId(3L);

        assertEquals(1, result.size());
        verify(roleAssignmentRepository, times(1)).findByProjectId(3L);
    }

    @Test
    void getRoleByUserAndProject_ShouldReturnOptionalRole() {
        when(roleAssignmentRepository.findByUserIdAndProjectId(2L, 3L))
            .thenReturn(List.of(roleAssignment));

        Optional<RoleAssignment> result = roleAssignmentService.getRoleByUserAndProject(2L, 3L);

        assertTrue(result.isPresent());
        assertEquals(roleAssignment, result.get());
        verify(roleAssignmentRepository, times(1)).findByUserIdAndProjectId(2L, 3L);
    }

    @Test
    void assignRole_ShouldSaveRoleAssignment() {
        when(roleAssignmentRepository.save(roleAssignment)).thenReturn(roleAssignment);

        RoleAssignment result = roleAssignmentService.assignRole(roleAssignment);

        assertEquals(roleAssignment, result);
        verify(roleAssignmentRepository, times(1)).save(roleAssignment);
    }

    @Test
    void removeRole_ShouldCallDeleteById() {
        doNothing().when(roleAssignmentRepository).deleteById(1L);

        roleAssignmentService.removeRole(1L);

        verify(roleAssignmentRepository, times(1)).deleteById(1L);
    }
}
