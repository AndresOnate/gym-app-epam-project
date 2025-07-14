package com.epam.gymapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.service.TrainingService;
import com.epam.gymapp.util.TransactionContext;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;


import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainings")
public class TrainingController {

    private static final Logger transactionLogger = LoggerFactory.getLogger("transactionLogger"); // Use transactionLogger
    private static final Logger operationLogger = LoggerFactory.getLogger("operationLogger"); // Use operationLogger

    private final TrainingService trainingService;
    private final Counter addTrainingCounter; // Assuming you have Micrometer configured

    public TrainingController(TrainingService trainingService, MeterRegistry meterRegistry) {
        this.trainingService = trainingService;
        this.addTrainingCounter = meterRegistry.counter("training_add_total", "action", "addTraining");
    }

    @PostMapping
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingDto trainingRequest) {
        String transactionId = TransactionContext.getTransactionId(); // Get transaction ID
        transactionLogger.info("[{}] Controller: POST /api/v1/trainings received. Request Body: {}", transactionId, trainingRequest.toString());
        operationLogger.debug("[{}] Controller: Attempting to add new training.", transactionId);

        addTrainingCounter.increment(); // Micrometer counter

        try {
            Training savedTraining = trainingService.save(trainingRequest);
            transactionLogger.info("[{}] Controller: Training saved successfully. Response: 201 Created. Training ID: {}", transactionId, savedTraining.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTraining.getId()); // Return ID or a DTO
        } catch (EntityNotFoundException e) {
            transactionLogger.error("[{}] Controller: Error adding training. Response: 404 Not Found. Message: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            transactionLogger.error("[{}] Controller: Unexpected error adding training. Response: 500 Internal Server Error. Message: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTraining(@PathVariable Long id) {
        String transactionId = TransactionContext.getTransactionId();
        transactionLogger.info("[{}] Controller: DELETE /api/v1/trainings/{} received.", transactionId, id);
        operationLogger.debug("[{}] Controller: Attempting to delete training with ID: {}", transactionId, id);

        try {
            trainingService.delete(id);
            transactionLogger.info("[{}] Controller: Training with ID {} deleted. Response: 204 No Content.", transactionId, id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            transactionLogger.error("[{}] Controller: Error deleting training. Response: 404 Not Found. Message: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            transactionLogger.error("[{}] Controller: Unexpected error deleting training with ID {}. Response: 500 Internal Server Error. Message: {}", transactionId, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}