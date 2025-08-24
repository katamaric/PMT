package com.example.pmt_backend.serializers;

import com.example.pmt_backend.models.User;
import com.example.pmt_backend.services.UserService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDeserializerTest {

    @Mock
    private UserService userService;

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    @InjectMocks
    private UserDeserializer userDeserializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deserialize_ShouldReturnUser_WhenUserExists() throws IOException {
        User user = new User();
        user.setId(1L);

        when(jsonParser.getLongValue()).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        User result = userDeserializer.deserialize(jsonParser, deserializationContext);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void deserialize_ShouldThrowException_WhenUserNotFound() throws IOException {
        when(jsonParser.getLongValue()).thenReturn(999L);
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userDeserializer.deserialize(jsonParser, deserializationContext));

        assertTrue(exception.getMessage().contains("User not found with ID: 999"));
        verify(userService, times(1)).getUserById(999L);
    }
}
