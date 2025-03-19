package com.epam.gymapp.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.gymapp.model.user.User;
import com.epam.gymapp.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

        // Check if the username already exists in the database
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            logger.error("Username '{}' already exists in the database.", user.getUsername());
            throw new IllegalArgumentException("Username already exists.");
        }

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

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            logger.error("User with username '{}' not found during authentication.", username);
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        if (!user.getPassword().equals(password)) {
            logger.error("Invalid password provided for user '{}'.", username);
            throw new RuntimeException("Invalid password");
        }

        logger.info("User with username '{}' successfully authenticated.", username);
        return user;
    }
}