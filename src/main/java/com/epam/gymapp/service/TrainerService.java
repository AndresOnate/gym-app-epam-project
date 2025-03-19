package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.repository.TrainerRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service layer for managing operations related to the Trainer entity.
 * This service class provides methods to interact with the underlying data
 * repository (TrainerDao) for creating, retrieving, updating, and deleting
 * Trainer objects.
 */
@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    private final TrainerRepository trainerRepository; // Field-Based Injection

    public TrainerService(TrainerRepository trainerRepository){
        this.trainerRepository = trainerRepository;
    }

    /**
     * Retrieves all trainers from the database.
     * 
     * @return a list of all trainers
     */
    public List<Trainer> getAll() {
        logger.info("Fetching all trainers...");
        return trainerRepository.findAll();
    }

    /**
     * Retrieves a trainer by their ID.
     * 
     * @param id the ID of the trainer to retrieve
     * @return the Trainer object with the given ID, or null if not found
     */
    public Trainer getById(Long id) {
        logger.info("Fetching trainer with ID: {}", id);
        return trainerRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new trainer to the database.
     * 
     * @param trainer the Trainer object to save
     * @return the saved Trainer object
     */
    public Trainer save(Trainer trainer) {
        logger.info("Saving new trainer: {}", trainer);
        return trainerRepository.save(trainer);
    }

    /**
     * Updates an existing trainer's information.
     * 
     * @param id             the ID of the trainer to update
     * @param updatedTrainer the updated Trainer object with new values
     * @return the updated Trainer object
     */
    public Trainer update(Long id, Trainer updatedTrainer) {
        logger.info("Updating trainer with ID: {}", id);
        return trainerRepository.findById(id)
        .map(existingTrainer -> {
            existingTrainer.setSpecialization(updatedTrainer.getSpecialization());
            Trainer savedTrainer = trainerRepository.save(existingTrainer);
            logger.info("Trainer with ID: {} successfully updated", id);
                return savedTrainer;
        })
        .orElseThrow(() -> {
            logger.error("Trainer with ID: {} not found", id);
            return new EntityNotFoundException("Trainer not found with ID: " + id);
        });
    }

    /**
     * Deletes a trainer from the database.
     * 
     * @param trainer the Trainer object to delete
     */
    public void delete(Long id) {
        logger.info("Deleting trainer: {}", id);
        try {
            trainerRepository.deleteById(id);
            logger.info("Trainer successfully deleted: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting trainer: {}", id, e);
            throw e;
        }
    }
}