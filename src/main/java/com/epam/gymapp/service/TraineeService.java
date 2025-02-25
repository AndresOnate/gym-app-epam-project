package com.epam.gymapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.repository.TraineeDao;

/**
 * Service layer for managing operations related to the Trainee entity.
 * This service class provides methods to interact with the underlying data
 * repository (TraineeDao) for creating, retrieving, updating, and deleting
 * Trainee objects.
 */
@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    @Autowired
    private TraineeDao traineeDao;

    public TraineeService(TraineeDao traineeDao){
        this. traineeDao = traineeDao;
    }
    
    /**
     * Retrieves all trainees from the database.
     * 
     * @return a list of all trainees
     */
    public List<Trainee> getAll() {
        logger.info("Fetching all trainees...");
        return traineeDao.getAll();
    }

    /**
     * Retrieves a trainee by their ID.
     * 
     * @param id the ID of the trainee to retrieve
     * @return the Trainee object with the given ID, or null if not found
     */
    public Trainee getById(Long id) {
        logger.info("Fetching trainee with ID: {}", id);
        return traineeDao.getById(id).orElse(null);
    }

    /**
     * Saves a new trainee to the database.
     * This method persists a new Trainee object using the DAO.
     * 
     * @param trainee the Trainee object to save
     * @return the saved Trainee object
     */
    public Trainee save(Trainee trainee) {
        logger.info("Saving new trainee: {}", trainee);
        return traineeDao.save(trainee);
    }

    /**
     * Updates an existing trainee's information.
     * 
     * @param id             the ID of the trainee to update
     * @param updatedTrainee the updated Trainee object with new values
     * @return the updated Trainee object
     */
    public Trainee update(Long id, Trainee updatedTrainee) {
        logger.info("Updating trainee with ID: {}", id);
        try {
            Trainee updated = traineeDao.update(id, updatedTrainee);
            logger.info("Trainee with ID: {} successfully updated", id);
            return updated;
        } catch (Exception e) {
            logger.error("Error updating trainee with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Deletes a trainee from the database.
     * 
     * @param trainee the Trainee object to delete
     */
    public void delete(Long id) {
        logger.info("Deleting trainee: {}", id);
        try {
            traineeDao.delete(id);
            logger.info("Trainee successfully deleted: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting trainee: {}", id, e);
            throw e;
        }
    }
    
}
