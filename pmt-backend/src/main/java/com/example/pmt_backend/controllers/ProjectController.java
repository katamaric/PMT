package com.example.pmt_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pmt_backend.dto.AddMemberRequest;
import com.example.pmt_backend.dto.ProjectRequest;
import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.User;
import com.example.pmt_backend.services.ProjectService;
import com.example.pmt_backend.services.UserService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/admin/{adminId}")
    public List<Project> getProjectsByAdminId(@PathVariable Long adminId) {
        return projectService.getProjectsByAdminId(adminId);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());

        Project createdProject = projectService.createProject(project, request.getAdminId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }
    
    @PostMapping("/{projectId}/invite-member")
    public ResponseEntity<?> inviteMemberToProject(
            @PathVariable Long projectId, 
            @RequestBody AddMemberRequest request, 
            @RequestParam Long adminId) {
        try {
            // Find user by email
            User user = userService.getUserByEmail(request.getEmail());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User with email " + request.getEmail() + " does not exist.");
            }

            // Check if role is valid (default to MEMBER if not provided)
            Role role = (request.getRole() != null) ? request.getRole() : Role.MEMBER;

            // Add the user to project
            Project updatedProject = projectService.addMemberToProjectWithRole(projectId, user, role, adminId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to add members.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<User>> getProjectMembers(@PathVariable Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get().getMembers());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}