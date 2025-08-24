package com.example.pmt_backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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
    
    @Test
    void getUserById_ShouldReturnUserIfExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getUserById_ShouldReturnEmptyIfNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
    }
    
    @Test
    void getUsersByRole_ShouldReturnCorrectUsers() {
        User member1 = new User();
        member1.setId(2L);
        member1.setRole(Role.MEMBER);

        User member2 = new User();
        member2.setId(3L);
        member2.setRole(Role.MEMBER);

        when(userRepository.findByRole(Role.MEMBER)).thenReturn(List.of(member1, member2));

        var result = userService.getUsersByRole(Role.MEMBER);

        assertEquals(2, result.size());
        assertTrue(result.contains(member1));
        assertTrue(result.contains(member2));
    }
    
    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void save_ShouldCallRepositorySave() {
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        userService.save(sampleUser);

        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    void createUser_ShouldKeepPredefinedRole() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("pass");
        adminUser.setRole(Role.ADMIN);

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User created = userService.createUser(adminUser);

        assertEquals(Role.ADMIN, created.getRole());
        verify(userRepository, times(1)).save(adminUser);
    }

    @Test
    void login_ShouldReturnFalseIfEmailNull() {
        User loginAttempt = new User();
        loginAttempt.setEmail(null);
        loginAttempt.setPassword("password");

        assertFalse(userService.login(loginAttempt));
    }

    @Test
    void login_ShouldReturnFalseIfPasswordNull() {
        User loginAttempt = new User();
        loginAttempt.setEmail("test@example.com");
        loginAttempt.setPassword(null);

        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);

        assertFalse(userService.login(loginAttempt));
    }
    
    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        var result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals(sampleUser.getUsername(), result.get(0).getUsername());
    }
}