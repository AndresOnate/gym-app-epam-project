package com.epam.gymapp.controller;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gymapp.dto.RegistrationDto;
import com.epam.gymapp.dto.TraineeProfileDto;
import com.epam.gymapp.dto.TrainerDto;
import com.epam.gymapp.dto.TrainerProfileDto;
import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.service.TrainerService;
import com.epam.gymapp.service.TrainingService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/trainers")
public class TrainerController {

    private TrainerService trainerService;
    private TrainingService trainingService;


    @Autowired
    public TrainerController(TrainerService trainerService, TrainingService trainingService) {
        this.trainingService = trainingService;
        this.trainerService = trainerService;
    }

    @PostMapping
    public ResponseEntity<RegistrationDto> registerTrainer(@Valid @RequestBody TrainerDto trainerDto) {
        return ResponseEntity.ok(trainerService.save(trainerDto));       
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileDto> getTraineeProfile(@PathVariable String username) {
        TrainerProfileDto profile = trainerService.getTrainerProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileDto> updateTrainerProfile(
            @PathVariable String username,
            @RequestBody @Valid TrainerProfileDto requestDto) {
        TrainerProfileDto updatedProfile = trainerService.updateTrainerProfile(username, requestDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerDto>> getUnassignedTrainers(@PathVariable String username) {
        List<TrainerDto> unassignedTrainers = trainerService.getUnassignedTrainers(username);
        return ResponseEntity.ok(unassignedTrainers);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(
        @PathVariable String username,
        @RequestParam(required = false) Date periodFrom,
        @RequestParam(required = false) Date periodTo,
        @RequestParam(required = false) String traineeName) {

        List<TrainingDto> trainings = trainingService.getTrainerTrainings(username, periodFrom, periodTo, traineeName);
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
        trainerService.updateStatus(username, isActive);
        return ResponseEntity.ok().build();
    }

}
