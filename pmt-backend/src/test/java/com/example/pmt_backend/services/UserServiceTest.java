package com.example.pmt_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.pmt_backend.dao.UserRepository;
import com.example.pmt_backend.exceptions.DuplicateException;
import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.User;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("password123");
        sampleUser.setRole(Role.ADMIN);
    }

    @Test
    void createUser_ShouldAssignDefaultRoleIfNull() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("pass1234");

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User created = userService.createUser(newUser);

        assertEquals(Role.MEMBER, created.getRole());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void createUser_ShouldThrowDuplicateException() {
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateException.class, () -> userService.createUser(sampleUser));
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);

        User found = userService.getUserByEmail("test@example.com");

        assertEquals("testuser", found.getUsername());
    }

    @Test
    void getUserByUsername_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);

        User found = userService.getUserByUsername("testuser");

        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void login_ShouldReturnTrueIfCredentialsMatch() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);

        User loginAttempt = new User();
        loginAttempt.setEmail("test@example.com");
        loginAttempt.setPassword("password123");

        assertTrue(userService.login(loginAttempt));
    }

    @Test
    void login_ShouldReturnFalseIfUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        User loginAttempt = new User();
        loginAttempt.setEmail("notfound@example.com");
        loginAttempt.setPassword("password");

        assertFalse(userService.login(loginAttempt));
    }

    @Test
    void login_ShouldReturnFalseIfPasswordMismatch() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);

        User loginAttempt = new User();
        loginAttempt.setEmail("test@example.com");
        loginAttempt.setPassword("wrongpassword");

        assertFalse(userService.login(loginAttempt));
    }
}