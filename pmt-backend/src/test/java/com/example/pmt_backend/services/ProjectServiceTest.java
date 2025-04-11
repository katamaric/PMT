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

        // Setup common test data
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
    }

    @Test
    void testCreateProject() {
        // Mock UserRepository to return admin user when fetched by ID
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project createdProject = projectService.createProject(project, 1L);

        // Assertions
        assertNotNull(createdProject);
        assertEquals("Test Project", createdProject.getName());
        assertEquals(1, createdProject.getMembers().size()); // admin should be the member
        assertEquals(Role.ADMIN, createdProject.getMembers().get(0).getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testCreateProject_AdminNotFound() {
        // Mock to return an empty optional when admin is not found
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Try creating the project
        assertThrows(RuntimeException.class, () -> {
            projectService.createProject(project, 1L);
        });

        verify(userRepository, times(1)).findById(1L);
        verify(projectRepository, never()).save(any(Project.class)); // save shouldn't be called
    }

    @Test
    void testAddMemberToProject_NotAdmin() {
        // Mock existing project and user
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        User newMember = new User();
        newMember.setId(2L);
        newMember.setRole(Role.MEMBER);

        // Try to add member with non-admin user
        assertThrows(RuntimeException.class, () -> {
            projectService.addMemberToProjectWithRole(1L, newMember, Role.MEMBER, 2L); // adminId is 2, not 1
        });

        verify(projectRepository, never()).save(project); // project shouldn't be saved
    }
    
    @Test
    void testAddMemberToProject() {
    	
        project.setMembers(new ArrayList<>());

        // Mock admin
        User admin = new User();
        admin.setId(1L); // Set the admin's ID to 1
        project.setAdmin(admin);

        // Mock existing project and user
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        User newMember = new User();
        newMember.setId(2L);
        newMember.setRole(Role.MEMBER);

        // Mock saving the new member and saving the updated project
        when(userRepository.save(newMember)).thenReturn(newMember);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Add member to project
        Project updatedProject = projectService.addMemberToProjectWithRole(1L, newMember, Role.MEMBER, 1L); // adminId is 1

        // Assertions
        assertEquals(1, updatedProject.getMembers().size()); // Only admin should be present initially
        assertTrue(updatedProject.getMembers().contains(newMember)); // New member should be added
        verify(projectRepository, times(1)).save(updatedProject); // Ensure projectRepository.save was called
    }


    public Project addMemberToProjectWithRole(Long projectId, User user, Role role, Long adminId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the request is from an admin
        if (!project.getAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Only project admins can add members.");
        }

        // Check if the user is already a member of the project
        Optional<User> existingMember = project.getMembers().stream()
                .filter(member -> member.getId().equals(user.getId()))
                .findFirst();

        if (existingMember.isPresent()) {
            // If the member is already part of the project, check and update the role if necessary
            User member = existingMember.get();
            if (!member.getRole().equals(role)) {
                member.setRole(role);
                userRepository.save(member); // Save the updated user role
            }
        } else {
            // If not a member, add the user to the project and set their role
            user.setRole(role);
            project.getMembers().add(user);
            userRepository.save(user); // Save the new member
        }

        return projectRepository.save(project); // Save the updated project
    }
    
    @Test
    void testAddMemberToProject_AlreadyMember() {
    	
        project.setMembers(new ArrayList<>());
        User admin = new User();
        admin.setId(1L); // Set the admin's ID
        project.setAdmin(admin); // Set the admin to the project

        // Mock the existing project and user
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // Mock an existing member
        User existingMember = new User();
        existingMember.setId(2L);
        existingMember.setRole(Role.MEMBER);

        // Add the existing member to the project manually
        project.getMembers().add(existingMember);

        // Mock saving the user
        when(userRepository.save(existingMember)).thenReturn(existingMember);
        
        // Mock saving the project
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Call the service method to update the role (same role, but it should still trigger a save)
        Project updatedProject = projectService.addMemberToProjectWithRole(1L, existingMember, Role.MEMBER, 1L); // adminId is 1

        // Assertions
        assertEquals(1, updatedProject.getMembers().size()); // The member should not be added again
        assertTrue(updatedProject.getMembers().contains(existingMember)); // Ensure the existing member is still there
        
        // Verify that the save method was called for the user
        verify(userRepository, times(1)).save(existingMember); // Ensure userRepository.save was called for the existing member
        verify(projectRepository, times(1)).save(updatedProject); // Ensure projectRepository.save was called for the project
    }

}
