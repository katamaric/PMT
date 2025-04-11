package com.example.pmt_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pmt_backend.dao.ProjectRepository;
import com.example.pmt_backend.dao.UserRepository;
import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.User;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository; 

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    
    public List<Project> getProjectsByAdminId(Long adminId) {
        return projectRepository.findByAdminId(adminId);
    }
    
    public Project createProject(Project project, Long adminId) {
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin user not found!"));
        
        project.setAdmin(admin);
        if (project.getMembers() == null) {
            project.setMembers(new ArrayList<>());
        }
        admin.setRole(Role.ADMIN);
        project.getMembers().add(admin);
        
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
    
    public Project addMemberToProjectWithRole(Long projectId, User user, Role role, Long adminId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the request is from an admin
        if (!project.getAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Only project admins can add members.");
        }

        // Check if the user is already a member of the project
        boolean isMember = project.getMembers().stream()
                .anyMatch(existingMember -> existingMember.getId().equals(user.getId()));

        if (isMember) {
            // Update the user's role instead of adding them again
            user.setRole(role);
            userRepository.save(user);
        } else {
            // Add new member with assigned role
            user.setRole(role);
            project.getMembers().add(user);
        }

        return projectRepository.save(project);
    }

}
