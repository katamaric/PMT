package com.example.pmt_backend.serializers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.pmt_backend.models.User;
import com.example.pmt_backend.services.UserService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

@Component
public class UserDeserializer extends JsonDeserializer<User> {

    @Autowired
    private UserService userService;

    @Override
    public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Long userId = p.getLongValue();
        return userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }
}