package com.example.pmt_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pmt_backend.models.Task;
import com.example.pmt_backend.models.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {
	
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByStatus(TaskStatus status);
	
}
