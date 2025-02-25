package com.epam.gymapp.model.user;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The User class represents a user in the gym application.
 * It includes details such as the user's ID, first name, last name, username, password, and active status.
 */
public class User {

    private static final Map<String, Integer> usernameCount = new ConcurrentHashMap<>();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    private Long id;            // The unique identifier for the user
    private String firstName;   // The first name of the user
    private String lastName;    // The last name of the user
    private String username;    // The username of the user
    private String password;    // The password of the user
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
    public User(Long id, String firstName, String lastName, Boolean isActive) {
        this.id = id;
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
     * Sets the first name of the user.
     *
     * @param firstName The new first name.
     * @throws IllegalArgumentException if the first name is null or empty.
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First Name is required.");
        }
        this.firstName = firstName;
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
     * Sets the last name of the user.
     *
     * @param lastName The new last name.
     * @throws IllegalArgumentException if the last name is null or empty.
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last Name is required.");
        }
        this.lastName = lastName;
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

}
