package com.example.pmt_backend.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskHistoryTest {

    @Test
    void testGettersAndSetters() {
        Task task = new Task();
        User user = new User();

        TaskHistory history = new TaskHistory();
        history.setId(1L);
        history.setTask(task);
        history.setModifiedBy(user);
        history.setChanges("Changed status to IN_PROGRESS");
        LocalDateTime now = LocalDateTime.now();
        history.setTimestamp(now);

        assertEquals(1L, history.getId());
        assertEquals(task, history.getTask());
        assertEquals(user, history.getModifiedBy());
        assertEquals("Changed status to IN_PROGRESS", history.getChanges());
        assertEquals(now, history.getTimestamp());
    }

    @Test
    void testAllArgsConstructor() {
        Task task = new Task();
        User user = new User();

        TaskHistory history = new TaskHistory(task, user, "Updated description");

        assertEquals(task, history.getTask());
        assertEquals(user, history.getModifiedBy());
        assertEquals("Updated description", history.getChanges());
        assertNotNull(history.getTimestamp()); // Timestamp should be initialized
    }
}
