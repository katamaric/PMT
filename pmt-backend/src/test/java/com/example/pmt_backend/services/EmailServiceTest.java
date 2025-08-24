package com.example.pmt_backend.services;

import static org.mockito.Mockito.*;

import com.example.pmt_backend.models.Task;
import com.example.pmt_backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.time.LocalDate;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("test@example.com");
        user.setUsername("TestUser");

        task = new Task();
        task.setName("Sample Task");
        task.setDescription("Sample Description");
        task.setDueDate(LocalDate.now());
    }

    @Test
    void sendTaskAssignmentNotification_ShouldCallMailSender() {
        emailService.sendTaskAssignmentNotification(user, task);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
    
    @Test
    void sendTaskAssignmentNotification_ShouldBuildCorrectMessage() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendTaskAssignmentNotification(user, task);

        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertTrue(sentMessage.getSubject().contains("Sample Task"));
        assertTrue(sentMessage.getText().contains("Sample Description"));
    }
}
