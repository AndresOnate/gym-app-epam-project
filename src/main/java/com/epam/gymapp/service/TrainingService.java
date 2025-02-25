package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.repository.TrainingDao;

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
    private TrainingDao trainingDao; // Field-Based Injection

    /**
     * Retrieves all trainings from the database.
     * 
     * @return a list of all trainings
     */
    public List<Training> getAll() {
        logger.info("Fetching all trainings");
        return trainingDao.getAll();
    }

    /**
     * Retrieves a training by its ID.
     * 
     * @param id the ID of the training to retrieve
     * @return the Training object with the given ID, or null if not found
     */
    public Training getById(Long id) {
        logger.info("Fetching training with ID: {}", id);
        return trainingDao.getById(id).orElse(null);
    }

    /**
     * Saves a new training session to the database.
     * 
     * @param training the Training object to save
     * @return the saved Training object
     */
    public Training save(Training training) {
        logger.info("Saving new training: {}", training);
        return trainingDao.save(training);
    }

    /**
     * Updates an existing training's information.
     * 
     * @param id the ID of the training to update
     * @param updatedTraining the updated Training object with new values
     * @return the updated Training object
     */
    public Training update(Long id, Training updatedTraining) {
        try {
            logger.info("Updating training with ID: {}", id);
            return trainingDao.update(id, updatedTraining);
        } catch (Exception e) {
            logger.error("Error updating training with ID: {}", id, e);
            return null;
        }
    }

    /**
     * Deletes a training from the database.
     * 
     * @param training the Training object to delete
     */
    public void delete(Long id) {
        try {
            logger.info("Deleting training: {}", id);
            trainingDao.delete(id);
        } catch (Exception e) {
            logger.error("Error deleting training: {}", id, e);
        }
    }
}