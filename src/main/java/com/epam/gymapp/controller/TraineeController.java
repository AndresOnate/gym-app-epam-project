package com.epam.gymapp.controller;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gymapp.dto.TraineeDto;
import com.epam.gymapp.dto.TraineeProfileDto;
import com.epam.gymapp.dto.TrainerAssignmentRequest;
import com.epam.gymapp.dto.TrainerDto;
import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.dto.RegistrationDto;

import com.epam.gymapp.service.TraineeService;
import com.epam.gymapp.service.TrainingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/trainees")
public class TraineeController {

    private TraineeService traineeService;
    private TrainingService trainingService; 


    @Autowired
    public TraineeController(TraineeService traineeService, TrainingService trainingService) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
    }

    @PostMapping
    public ResponseEntity<RegistrationDto> registerTrainee(@Valid @RequestBody TraineeDto traineeDto) {
        return ResponseEntity.ok(traineeService.save(traineeDto));       
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileDto> getTraineeProfile(@PathVariable String username) {
        TraineeProfileDto profile = traineeService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeProfileDto> updateTraineeProfile(
            @PathVariable String username,
            @Valid @RequestBody TraineeProfileDto request) {
        TraineeProfileDto updatedProfile = traineeService.updateProfile(username, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        traineeService.deleteTraineeByUsername(username);
        return ResponseEntity.ok("Learning profile successfully removed.");
    }

    
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerDto>> updateTraineeTrainers(
            @PathVariable String username,
            @RequestBody TrainerAssignmentRequest request) {
        List<TrainerDto> updatedTrainers = traineeService.updateTraineeTrainers(username, request.getTrainers());
        return ResponseEntity.ok(updatedTrainers);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) Date periodFrom,
            @RequestParam(required = false) Date periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {
    
        List<TrainingDto> trainings = trainingService.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType);
        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/{username}/status")
    public ResponseEntity<?> updateTraineeStatus(
            @PathVariable String username,
            @RequestBody Map<String, Boolean> statusUpdate) {
        Boolean isActive = statusUpdate.get("isActive");
        if (isActive == null) {
            return ResponseEntity.badRequest().body("El campo 'isActive' es obligatorio.");
        }
        traineeService.updateStatus(username, isActive);
        return ResponseEntity.ok().build();
    }



  
}
