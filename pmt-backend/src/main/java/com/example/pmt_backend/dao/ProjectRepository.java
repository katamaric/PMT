package com.example.pmt_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pmt_backend.models.Project;
import com.example.pmt_backend.models.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	
	List<Project> findByAdminId(Long adminId);

	List<Project> findByMembersContaining(User user);
}
