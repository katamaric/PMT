package com.example.pmt_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.pmt_backend.dao.UserRepository;
import com.example.pmt_backend.exceptions.DuplicateException;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        try {
        	
        	if (user.getRole() == null) {
                user.setRole(Role.MEMBER);
            }
            
            return userRepository.save(user);
            
        } catch (DataIntegrityViolationException e) {
        	
            throw new DuplicateException("User with this email or username already exists.");
        }
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getAllUsersWithProjectsAndTasks() {
        List<User> users = userRepository.findAll();
        
        users.forEach(user -> {
            user.getProjects().size();
            user.getAssignedTasks().size();
        });

        return users;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean login(User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser == null) {
            return false; 
        }

        return existingUser.getPassword().equals(user.getPassword());
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
