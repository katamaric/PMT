package com.example.pmt_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pmt_backend.models.RoleAssignment;

public interface RoleAssignmentRepository extends JpaRepository<RoleAssignment, Long> {
	
    List<RoleAssignment> findByUserId(Long userId);
    List<RoleAssignment> findByProjectId(Long projectId);
    List<RoleAssignment> findByUserIdAndProjectId(Long userId, Long projectId);
}
