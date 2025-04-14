package com.epam.gymapp.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TraineeProfileDto {

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    @NotNull
    private boolean isActive;
    private List<TrainerDto> trainers;

    public TraineeProfileDto() {
    }

    public TraineeProfileDto(String firstName, String lastName, LocalDate dateOfBirth, String address, boolean isActive, List<TrainerDto> trainers) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
        this.trainers = trainers;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<TrainerDto> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerDto> trainers) {
        this.trainers = trainers;
    }

    
    
}
