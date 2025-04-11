package com.example.pmt_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pmt_backend.models.TaskHistory;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskId(Long taskId);
}
