package com.epam.gymapp.model.trainer;

import org.hibernate.annotations.ManyToAny;

import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Represents a trainer in the gym system.
 * Extends the {@code User} class, inheriting basic user attributes and methods.
 */
@Entity
@Table(name = "trainers")
public class Trainer extends User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             // The unique identifier of the trainer

    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;   // The area of specialization of the trainer

    /**
     * Default constructor for the {@code Trainer} class.
     */
    public Trainer() {}

    /**
     * Constructs a trainer with all attributes initialized.
     *
     * @param id Unique identifier of the trainer.
     * @param firstName First name of the trainer.
     * @param lastName Last name of the trainer.
     * @param username Trainer's username.
     * @param password Trainer's password.
     * @param isActive Activation status of the trainer in the system.
     * @param trainingType The type of training the trainer provides.
     * @param specialization The trainer's area of specialization.
     */
    public Trainer(String firstName, String lastName, Boolean isActive, TrainingType trainingType) {
        super(firstName, lastName, isActive);
        this.specialization = trainingType;
    }
    
    /**
     * Gets the trainer's specialization.
     *
     * @return The trainer's specialization.
     */
    public TrainingType getSpecialization() {
        return specialization;
    }

    /**
     * Sets the trainer's specialization.
     *
     * @param specialization The new specialization.
     */
    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }

    /**
     * Returns a string representation of the trainer.
     *
     * @return A string containing the trainer's details.
     */
    @Override
    public String toString() {
    return "Trainer{" +
           "id=" + getId() +  
           ", firstName='" + getFirstName() + '\'' +  
           ", lastName='" + getLastName() + '\'' + 
           ", isActive=" + getIsActive() + 
           ", specialization=" + specialization +
           '}';
    }

}
