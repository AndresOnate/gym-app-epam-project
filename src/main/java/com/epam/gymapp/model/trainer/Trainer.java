package com.epam.gymapp.model.trainer;

import com.epam.gymapp.model.training.TrainingType;
import com.epam.gymapp.model.user.User;

/**
 * Represents a trainer in the gym system.
 * Extends the {@code User} class, inheriting basic user attributes and methods.
 */
public class Trainer extends User{

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
    public Trainer(Long id, String firstName, String lastName, Boolean isActive, TrainingType trainingType) {
        super(id, firstName, lastName, isActive);
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
