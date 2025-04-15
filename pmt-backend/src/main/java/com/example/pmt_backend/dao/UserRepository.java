package com.example.pmt_backend.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pmt_backend.models.Role;
import com.example.pmt_backend.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findByRole(Role role);
    Optional<User> findByEmailAndPassword(String email, String password);
}
