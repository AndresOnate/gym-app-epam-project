package com.epam.gymapp.model.trainee;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import com.epam.gymapp.dto.TraineeDto;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Represents a trainee in the gym system.
 * Extends the {@code User} class, inheriting basic user attributes and methods.
 */
@Entity
@Table(name = "trainees")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainee_id")
    private Long id;             // The unique identifier of the trainee

    @Column(nullable = true)
    private Date dateOfBirth;        // The date of birth of the trainee

    @Column(nullable = true)
    private String address;         // The address of the trainee

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
        name = "trainee_trainer",  // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "trainee_id"),  // Clave foránea hacia Trainee
        inverseJoinColumns = @JoinColumn(name = "trainer_id")  // Clave foránea hacia Trainer
    )
    private Set<Trainer> trainers = new HashSet<>();  // Set de Trainers asociados con el Trainee


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
    public Trainee(Date dateOfBirth, String address) {
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Trainee(Long id, User user, Date dateOfBirth, String address, Set<Trainer> trainers) {
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.trainers = trainers;
    }

    public Trainee(TraineeDto traineeDto) {
        this.user = new User(traineeDto.getFirstName(), traineeDto.getLastName(), true);
        this.dateOfBirth = traineeDto.getDateOfBirth();
        this.address = traineeDto.getAddress();    
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
     * Retrieves the user associated with this trainee.
     * 
     * This method returns the reference to the {@link User} object representing
     * the user linked to the trainee. The relationship is one-to-one.
     *
     * @return The {@link User} object associated with this trainee.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this trainee.
     * 
     * This method assigns a {@link User} object to this trainee. It establishes
     * the one-to-one relationship between the trainee and the user.
     *
     * @param user The {@link User} object representing the user to be associated with the trainee.
     * @throws IllegalArgumentException If the provided user is {@code null}.
     */
    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        this.user = user;
    }

    /**
     * Gets the trainers associated with this trainee.
     *
     * @return The set of {@link Trainer} objects associated with this trainee.
     */
    public Set<Trainer> getTrainers() {
        return trainers;
    }

    /**
     * Sets the trainers for this trainee.
     *
     * @param trainers The set of {@link Trainer} objects to be associated with this trainee.
     */
    public void setTrainers(Set<Trainer> trainers) {
        if (trainers == null) {
            throw new IllegalArgumentException("Trainers cannot be null.");
        }
        this.trainers = trainers;
    }


    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Returns a string representation of the trainee.
     *
     * @return A string containing the trainee's details.
     */
    @Override
    public String toString() {
        return "Trainee{id=" + id + ", firstName='" + user.getFirstName() + "', lastName='" + user.getLastName() + 
               "', username='" + user.getUsername() + "', dateOfBirth=" + dateOfBirth + ", address='" + address + "'}";
    }
}
