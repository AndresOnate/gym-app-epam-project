package com.epam.gymapp.dto;

import java.util.List;

public class TrainerAssignmentRequest {

    private List<String> trainers;

    public TrainerAssignmentRequest() {
    }

    public TrainerAssignmentRequest(List<String> trainers) {
        this.trainers = trainers;
    }

    public List<String> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<String> trainers) {
        this.trainers = trainers;
    }
    
}
