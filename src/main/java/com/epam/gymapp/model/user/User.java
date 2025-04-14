package com.epam.gymapp.model.user;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

/**
 * The User class represents a user in the gym application.
 * It includes details such as the user's ID, first name, last name, username, password, and active status.
 */

 @Entity
 @Table(name = "users")
public class User {

    private static final Map<String, Integer> usernameCount = new ConcurrentHashMap<>();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;            // The unique identifier for the user

    @Column(nullable = false)
    private String firstName;   // The first name of the user

    @Column(nullable = false)
    private String lastName;    // The last name of the user

    @Column(unique = true, nullable = false)
    private String username;    // The username of the user

    @Column(nullable = false)
    private String password;    // The password of the user

    @Column(nullable = false)
    private boolean isActive;   // The active status of the user

    public User() {
    }

    /**
     * Parameterized constructor to initialize a User instance.
     *
     * @param id        Unique identifier of the user.
     * @param firstName First name of the user.
     * @param lastName  Last name of the user.
     * @param username  Unique username of the user.
     * @param password  Password for the user's account.
     * @param isActive  Status indicating if the user is active.
     */
    public User(String firstName, String lastName, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = generateUsername();
        this.password = generatePassword();
        this.isActive = isActive;
    }

        /**
     * Generates a unique username based on the user's first and last name.
     * If a username with the same first and last name already exists, a numeric suffix is appended.
     *
     * @return A unique username in the format "FirstName.LastName" or "FirstName.LastNameX" (where X is a number).
     */
    private String generateUsername() {
        String baseUsername = firstName + "." + lastName;
        int count = usernameCount.getOrDefault(baseUsername, 0);
        String uniqueUsername = (count == 0) ? baseUsername : baseUsername + count;
        usernameCount.put(baseUsername, count + 1);
        return uniqueUsername;
    }

    /**
     * Generates a secure random password of 10 characters.
     * The password includes uppercase and lowercase letters, numbers, and special characters.
     *
     * @return A randomly generated password of length 10.
     */
    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Gets the user ID.
     *
     * @return User's unique identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id The new ID for the user.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the first name of the user.
     *
     * @return The user's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the user.
     *
     * @return The user's last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the username of the user.
     *
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username.
     * @throws IllegalArgumentException if the username is null or empty.
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        this.username = username;
    }

    /**
     * Gets the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The new password.
     * @throws IllegalArgumentException if the password is null or empty.
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        this.password = password;
    }

    /**
     * Gets the user's active status.
     *
     * @return {@code true} if the user is active, {@code false} otherwise.
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets the user's active status.
     *
     * @param isActive The new active status.
     * @throws IllegalArgumentException if the active status is null.
     */
    public void setIsActive(Boolean isActive) {
        if (isActive == null) {
            throw new IllegalArgumentException("Account status (Active/Inactive) is required.");
        }
        this.isActive = isActive;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    

}
