package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.repository.TrainingRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service layer for managing operations related to the Training entity.
 * This service class provides methods to interact with the underlying data
 * repository (TrainingDao) for creating, retrieving, updating, and deleting
 * Training objects.
 */
@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    private TrainingRepository trainingRepository; // Field-Based Injection

    /**
     * Retrieves all trainings from the database.
     * 
     * @return a list of all trainings
     */
    public List<Training> getAll() {
        logger.info("Fetching all trainings");
        return trainingRepository.findAll();
    }

    /**
     * Retrieves a training by its ID.
     * 
     * @param id the ID of the training to retrieve
     * @return the Training object with the given ID, or null if not found
     */
    public Training getById(Long id) {
        logger.info("Fetching training with ID: {}", id);
        return trainingRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new training session to the database.
     * 
     * @param training the Training object to save
     * @return the saved Training object
     */
    public Training save(Training training) {
        logger.info("Saving new training: {}", training);
        return trainingRepository.save(training);
    }

    /**
     * Updates an existing training's information.
     * 
     * @param id the ID of the training to update
     * @param updatedTraining the updated Training object with new values
     * @return the updated Training object
     */
    public Training update(Long id, Training updatedTraining) {
        logger.info("Updating training with ID: {}", id);

        return trainingRepository.findById(id)
                .map(existingTraining -> {
                    existingTraining.setTrainingName(updatedTraining.getTrainingName());
                    existingTraining.setTrainingDuration(updatedTraining.getTrainingDuration());
                    existingTraining.setTrainingDate(updatedTraining.getTrainingDate());
                    existingTraining.setTrainingType(updatedTraining.getTrainingType());
                    Training savedTraining = trainingRepository.save(existingTraining);
                    logger.info("Training with ID: {} successfully updated", id);
                    return savedTraining;
                })
                .orElseThrow(() -> {
                    logger.error("Training with ID: {} not found", id);
                    return new EntityNotFoundException("Training not found with ID: " + id);
                });
    }
    
    /**
     * Deletes a training from the database.
     * 
     * @param training the Training object to delete
     */
    public void delete(Long id) {
        try {
            logger.info("Deleting training: {}", id);
            trainingRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting training: {}", id, e);
        }
    }
}