package com.example.pmt_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.pmt_backend.models.Task;
import com.example.pmt_backend.models.User;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTaskAssignmentNotification(User assignedUser, Task task) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(assignedUser.getEmail());
        message.setSubject("New Task Assigned: " + task.getName());
        message.setText("Hello " + assignedUser.getUsername() + ",\n\n"
                      + "You have been assigned a new task: " + task.getName() + ".\n"
                      + "Description: " + task.getDescription() + "\n"
                      + "Due Date: " + task.getDueDate() + "\n\n"
                      + "Best regards,");

        mailSender.send(message);
    }
}