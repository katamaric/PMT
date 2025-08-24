package com.example.pmt_backend.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.pmt_backend.dao.ProjectRepository;
import com.example.pmt_backend.dao.UserRepository;
import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private User admin;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        admin = new User();
        admin.setId(1L);
        admin.setUsername("adminUser");
        admin.setEmail("admin@pmt.com");
        admin.setRole(Role.ADMIN);

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("A sample project");
        project.setStartDate(LocalDate.now());
        project.setMembers(new ArrayList<>());
        project.setAdmin(admin);
    }

    @Test
    void testCreateProject_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project createdProject = projectService.createProject(project, 1L);

        assertNotNull(createdProject);
        assertEquals("Test Project", createdProject.getName());
        assertEquals(1, createdProject.getMembers().size());
        assertEquals(Role.ADMIN, createdProject.getMembers().get(0).getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testCreateProject_AdminNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.createProject(project, 1L));

        verify(userRepository, times(1)).findById(1L);
        verify(projectRepository, never()).save(any(Project.class));
    }

    void testAddMemberToProject_ExistingMember_UpdateRole() {
        User existingMember = new User();
        existingMember.setId(2L);
        existingMember.setRole(Role.MEMBER);
        project.getMembers().add(existingMember);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.save(existingMember)).thenReturn(existingMember);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project updatedProject = projectService.addMemberToProjectWithRole(1L, existingMember, Role.ADMIN, 1L);

        assertEquals(2, updatedProject.getMembers().size()); // admin + existing member
        assertEquals(Role.ADMIN, existingMember.getRole());
        verify(userRepository, times(1)).save(existingMember);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void testAddMemberToProject_NotAdminThrows() {
        User newMember = new User();
        newMember.setId(3L);
        newMember.setRole(Role.MEMBER);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(RuntimeException.class, () ->
            projectService.addMemberToProjectWithRole(1L, newMember, Role.MEMBER, 999L)
        );

        verify(projectRepository, never()).save(any(Project.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<Project> result = projectService.getAllProjects();
        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void testGetProjectById_Found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<Project> result = projectService.getProjectById(1L);
        assertTrue(result.isPresent());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.getProjectById(999L);
        assertFalse(result.isPresent());
        verify(projectRepository, times(1)).findById(999L);
    }

    @Test
    void testGetProjectsByAdminId_NoProjects() {
        when(projectRepository.findByAdminId(1L)).thenReturn(new ArrayList<>());

        List<Project> result = projectService.getProjectsByAdminId(1L);
        assertTrue(result.isEmpty());
        verify(projectRepository, times(1)).findByAdminId(1L);
    }

    @Test
    void testGetProjectsByUser_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            projectService.getProjectsByUser(999L)
        );

        verify(userRepository, times(1)).findById(999L);
        verify(projectRepository, never()).findByMembersContaining(any());
    }

    @Test
    void testGetProjectsByUser_Found() {
        User normalUser = new User();
        normalUser.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
        when(projectRepository.findByMembersContaining(normalUser)).thenReturn(List.of(project));

        List<Project> result = projectService.getProjectsByUser(2L);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findById(2L);
        verify(projectRepository, times(1)).findByMembersContaining(normalUser);
    }

    @Test
    void testDeleteProject() {
        doNothing().when(projectRepository).deleteById(1L);

        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).deleteById(1L);
    }
}
