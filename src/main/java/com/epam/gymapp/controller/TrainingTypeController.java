package com.epam.gymapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.service.TrainingTypeService;

@RestController
@RequestMapping("/api/v1/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        return ResponseEntity.ok(trainingTypes);
    }
}