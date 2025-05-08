package com.epam.gymapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.service.TrainingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final Counter addTrainingCounter;

    @Autowired
    public TrainingController(TrainingService trainingService, MeterRegistry meterRegistry) {
        this.trainingService = trainingService;
        this.addTrainingCounter = meterRegistry.counter("training_add_total", "action", "addTraining");
    }

    @PostMapping
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingDto trainingRequest) {
        addTrainingCounter.increment();
        trainingService.save(trainingRequest);
        return ResponseEntity.status(201).build();
    }
}