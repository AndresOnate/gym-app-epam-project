package com.epam.gymapp.service;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.epam.gymapp.exception.NotFoundException;
import com.epam.gymapp.model.user.User;
import com.epam.gymapp.repository.UserRepository;

/**
 * Service layer for managing operations related to the User entity.
 * This service class provides methods to interact with the underlying data
 * repository (UserRepository) for creating, retrieving, updating, and deleting
 * User objects, as well as handling authentication, password changes, and user status updates.
 */
@Service
public class UserService implements UserDetailsService{

    private LoginAttemptService loginAttemptService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Saves a new user to the database, ensuring that the username is unique.
     *
     * @param user The User object to save.
     * @return The saved User object.
     * @throws IllegalArgumentException if the username already exists.
     */
    public User save(User user) {
        logger.info("Attempting to save a new user with username: {}", user.getUsername());
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        System.out.println("existingUser: " + existingUser);
        if (existingUser.isPresent()) {
            logger.error("Username '{}' already exists in the database.", user.getUsername());
            throw new IllegalArgumentException("Username already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User with username '{}' successfully saved.", savedUser.getUsername());
        return savedUser;
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The User object if found, or an empty Optional if not found.
     */
    public Optional<User> findByUsername(String username) {
        logger.info("Searching for user with username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.info("User with username '{}' found.", username);
        } else {
            logger.warn("User with username '{}' not found.", username);
        }
        return user;
    }

    /**
     * Authenticates a user by checking if the credentials are valid.
     *
     * @param username The username.
     * @param password The password.
     * @return The User object if credentials are correct.
     * @throws RuntimeException If the credentials are incorrect.
     */
    public User authenticate(String username, String password) {
        logger.info("Attempting to authenticate user with username: {}", username);

        if (loginAttemptService.isBlocked(username)) {
            long secondsLeft = loginAttemptService.getRemainingBlockTime(username);
            throw new RuntimeException("User is blocked. Try again in " + secondsLeft + " seconds.");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            logger.error("User with username '{}' not found during authentication.", username);
            throw new NotFoundException("User not found");
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(username);
            logger.error("Invalid password provided for user '{}'.", username);
            throw new BadCredentialsException("Invalid password");
        }
        loginAttemptService.loginSucceeded(username);
        logger.info("User with username '{}' successfully authenticated.", username);
        return user;
    }

        /**
     * Changes the password of a user if the old password matches.
     *
     * @param username    The username of the user.
     * @param oldPassword The current password of the user.
     * @param newPassword The new password to set.
     * @throws RuntimeException If the old password doesn't match or if any error occurs.
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        logger.info("Attempting to change password for user: {}", username);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters long.");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        logger.info("Password changed successfully for user: {}", username);
    }

    /**
     * Activates or deactivates a user based on the provided status.
     *
     * @param username The username of the user.
     * @param isActive The new active status (true for activation, false for deactivation).
     * @throws RuntimeException If the user is not found.
     */
    public void changeUserStatus(String username, boolean isActive) {
        logger.info("Changing status for user: {} to {}", username, isActive ? "ACTIVE" : "INACTIVE");
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        user.setIsActive(isActive);
        userRepository.save(user);
        logger.info("User {} is now {}", username, isActive ? "ACTIVE" : "INACTIVE");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

}