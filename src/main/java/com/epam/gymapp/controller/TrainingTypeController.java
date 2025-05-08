package com.epam.gymapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.service.TrainingTypeService;

@RestController
@RequestMapping("/api/v1/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final Counter getAllTrainingTypesCounter;

    @Autowired
    public TrainingTypeController(TrainingTypeService trainingTypeService, MeterRegistry meterRegistry) {
        this.trainingTypeService = trainingTypeService;
        this.getAllTrainingTypesCounter = meterRegistry.counter("training_types_get_all_total", "action", "getAllTrainingTypes");
    }

    @GetMapping
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        getAllTrainingTypesCounter.increment();
        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        return ResponseEntity.ok(trainingTypes);
    }
}