package com.epam.gymapp.model.training;

import java.sql.Date;

/**
 * The Training class represents a training session in the gym application.
 * It includes details such as the training ID, trainee ID, trainer ID, training name,
 * training type, training date, and training duration.
 */
public class Training {

    private Long id;                  // The unique identifier for the training session
    private Long traineeId;           // The ID of the trainee associated with the training
    private Long trainerId;           // The ID of the trainer associated with the training
    private String trainingName;      // The name of the training session
    private TrainingType trainingType; // The type of training
    private Date trainingDate;        // The date of the training session
    private int trainingDuration;     // The duration of the training session in minutes


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
    public Training(Long id, Long traineeId, Long trainerId, String trainingName, TrainingType trainingType, Date trainingDate, int trainingDuration) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
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
    public Long getTraineeId() {
        return traineeId;
    }

    /**
     * Sets the trainee's identifier.
     *
     * @param traineeId The new trainee ID.
     */
    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }

    /**
     * Gets the trainer's identifier.
     *
     * @return The trainer ID.
     */
    public Long getTrainerId() {
        return trainerId;
    }

    /**
     * Sets the trainer's identifier.
     *
     * @param trainerId The new trainer ID.
     */
    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
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
                ", trainee=" + traineeId +
                ", trainer=" + trainerId +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}
