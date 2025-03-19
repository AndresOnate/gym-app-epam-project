package com.epam.gymapp.model.training;

import java.sql.Date;

import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.trainingType.TrainingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**
 * The Training class represents a training session in the gym application.
 * It includes details such as the training ID, trainee ID, trainer ID, training name,
 * training type, training date, and training duration.
 */
@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;      // The ID of the trainee associated with the training
    
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;        // The ID of the trainer associated with the training

    @Column(nullable = false)
    private String trainingName;      // The name of the training session

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType; // The type of training

    @Column(nullable = false)
    private Date trainingDate;        // The date of the training session

    @Column(nullable = false)
    private int trainingDuration;     // The duration of the training session in minutes

    public Training() {
    }

    /**
     * Constructs a {@code Training} object with the specified attributes.
     *
     * @param id Unique identifier for the training session.
     * @param traineeId Identifier of the trainee.
     * @param trainerId Identifier of the trainer.
     * @param trainingName Name of the training session.
     * @param trainingType Type of training being conducted.
     * @param trainingDate Date of the training session.
     * @param trainingDuration Duration of the training session in minutes.
     */
    public Training(Trainee trainee, Trainer trainer, TrainingType trainingType, String trainingName, Date trainingDate, Integer trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingType = trainingType;
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    /**
     * Gets the unique identifier of the training session.
     *
     * @return The training session ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the training session.
     *
     * @param id The new training session ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the trainee's identifier.
     *
     * @return The trainee ID.
     */
    public Trainee getTrainee() {
        return trainee;
    }

    /**
     * Sets the trainee's identifier.
     *
     * @param traineeId The new trainee ID.
     */
    public void setTraineeId(Trainee trainee) {
        this.trainee = trainee;
    }

    /**
     * Gets the trainer's identifier.
     *
     * @return The trainer ID.
     */
    public Trainer getTrainer() {
        return trainer;
    }

    /**
     * Sets the trainer's identifier.
     *
     * @param trainerId The new trainer ID.
     */
    public void setTrainerId(Trainer trainer) {
        this.trainer = trainer;
    }

    /**
     * Gets the name of the training session.
     *
     * @return The training name.
     */
    public String getTrainingName() {
        return trainingName;
    }

    /**
     * Sets the name of the training session.
     *
     * @param trainingName The new training name.
     */
    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    /**
     * Gets the type of training being conducted.
     *
     * @return The training type.
     */
    public TrainingType getTrainingType() {
        return trainingType;
    }

    /**
     * Sets the type of training being conducted.
     *
     * @param trainingType The new training type.
     */
    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    /**
     * Gets the date of the training session.
     *
     * @return The training date.
     */
    public Date getTrainingDate() {
        return trainingDate;
    }

    /**
     * Sets the date of the training session.
     *
     * @param trainingDate The new training date.
     */
    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    /**
     * Gets the duration of the training session in minutes.
     *
     * @return The training duration.
     */
    public int getTrainingDuration() {
        return trainingDuration;
    }

    /**
     * Sets the duration of the training session in minutes.
     *
     * @param trainingDuration The new training duration.
     */
    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    /**
     * Returns a string representation of the training session.
     *
     * @return A string containing the training details.
     */
    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", trainee=" + trainee.getUser().getUsername() +
                ", trainer=" + trainee.getUser().getUsername() +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}
