package com.example.pmt_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pmt_backend.dao.RoleAssignmentRepository;
import com.example.pmt_backend.models.RoleAssignment;

@Service
public class RoleAssignmentService {

    @Autowired
    private RoleAssignmentRepository roleAssignmentRepository;

    public List<RoleAssignment> getAllRoles() {
        return roleAssignmentRepository.findAll();
    }

    public List<RoleAssignment> getRolesByUserId(Long userId) {
        return roleAssignmentRepository.findByUserId(userId);
    }

    public List<RoleAssignment> getRolesByProjectId(Long projectId) {
        return roleAssignmentRepository.findByProjectId(projectId);
    }

    public Optional<RoleAssignment> getRoleByUserAndProject(Long userId, Long projectId) {
        return roleAssignmentRepository.findByUserIdAndProjectId(userId, projectId).stream().findFirst();
    }

    public RoleAssignment assignRole(RoleAssignment roleAssignment) {
        return roleAssignmentRepository.save(roleAssignment);
    }

    public void removeRole(Long id) {
        roleAssignmentRepository.deleteById(id);
    }
}
