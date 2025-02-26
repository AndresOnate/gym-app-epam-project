package com.epam.gymapp.model.trainee;

import java.sql.Date;

import com.epam.gymapp.model.user.User;

/**
 * Represents a trainee in the gym system.
 * Extends the {@code User} class, inheriting basic user attributes and methods.
 */
public class Trainee extends User{

    private Date dateOfBirth;        // The date of birth of the trainee
    private String address;         // The address of the trainee

    public Trainee() {}

    /**
     * Constructs a trainee with all attributes initialized.
     *
     * @param id Unique identifier of the trainee.
     * @param firstName First name of the trainee.
     * @param lastName Last name of the trainee.
     * @param username Trainee's username.
     * @param password Trainee's password.
     * @param isActive Activation status of the trainee in the system.
     * @param dateOfBirth Trainee's date of birth.
     * @param address Trainee's address.
     */
    public Trainee(Long id, String firstName, String lastName, Boolean isActive, Date dateOfBirth, String address) {
        super(id, firstName, lastName, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    /**
     * Gets the trainee's date of birth.
     *
     * @return The trainee's date of birth.
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Gets the trainee's address.
     *
     * @return The trainee's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the trainee's address.
     *
     * @param address The new address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns a string representation of the trainee.
     *
     * @return A string containing the trainee's details.
     */
    @Override
    public String toString() {
        return "Trainee{id=" + getId() + ", firstName='" + getFirstName() + "', lastName='" + getLastName() + 
               "', username='" + getUsername() + "', dateOfBirth=" + dateOfBirth + ", address='" + address + "'}";
    }
}
