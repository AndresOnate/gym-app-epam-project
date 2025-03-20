package com.epam.gymapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.gymapp.model.user.User;
import com.epam.gymapp.repository.UserRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser =  new User("john", "doe", true);;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave_UserNotExists() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        User savedUser = userService.save(mockUser);
        assertEquals("john.doe", savedUser.getUsername());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void testSave_UserExists_ThrowsException() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockUser));
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(mockUser);
        });
        assertEquals("Username already exists.", thrown.getMessage());
    }

    @Test
    public void testAuthenticate_Success() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockUser));
        User authenticatedUser = userService.authenticate("john.doe", mockUser.getPassword());
        assertNotNull(authenticatedUser);
        assertEquals("john.doe", authenticatedUser.getUsername());
        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    public void testAuthenticate_Fail_InvalidPassword() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.authenticate("john_doe", "wrong_password");
        });

        assertEquals("Invalid password", thrown.getMessage());
    }

    @Test
    public void testChangePassword_Success() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(mockUser));
        userService.changePassword("john.doe", mockUser.getPassword(), "newpassword456");
        assertEquals("newpassword456", mockUser.getPassword());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void testChangePassword_Fail_WrongOldPassword() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("john_doe", "wrongpassword", "newpassword456");
        });

        assertEquals("Old password is incorrect", thrown.getMessage());
    }

    @Test
    public void testChangeUserStatus_Activate() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        userService.changeUserStatus("john_doe", true);
        assertTrue(mockUser.getIsActive());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void testChangeUserStatus_Deactivate() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        userService.changeUserStatus("john_doe", false);
        assertFalse(mockUser.getIsActive());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    public void testChangeUserStatus_UserNotFound() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.changeUserStatus("john_doe", true);
        });

        assertEquals("User not found", thrown.getMessage());
    }
}