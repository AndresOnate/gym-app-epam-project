package com.epam.gymapp.dto;

import java.time.LocalDate;

public class TrainerWorkloadRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive; // Represents trainer's current status
    private LocalDate trainingDate;
    private Integer trainingDuration; // in hours or minutes
    private ActionType actionType;


    public TrainerWorkloadRequest() {}

    public TrainerWorkloadRequest(String trainerUsername, String trainerFirstName, String trainerLastName,
                                  Boolean isActive, LocalDate trainingDate, Integer trainingDuration, ActionType actionType) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.actionType = actionType;
    }

    // Getters and Setters
    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }
    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String trainerFirstName) { this.trainerFirstName = trainerFirstName; }
    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String trainerLastName) { this.trainerLastName = trainerLastName; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }
    public Integer getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(Integer trainingDuration) { this.trainingDuration = trainingDuration; }
    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }

    @Override
    public String toString() {
        return "TrainerWorkloadRequest{" +
               "trainerUsername='" + trainerUsername + '\'' +
               ", trainerFirstName='" + trainerFirstName + '\'' +
               ", trainerLastName='" + trainerLastName + '\'' +
               ", isActive=" + isActive +
               ", trainingDate=" + trainingDate +
               ", trainingDuration=" + trainingDuration +
               ", actionType=" + actionType +
               '}';
    }
}