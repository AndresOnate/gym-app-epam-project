package com.epam.gymapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.service.TrainingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingDto trainingRequest) {
        trainingService.save(trainingRequest);
        return ResponseEntity.status(201).build();
    }
    
}
