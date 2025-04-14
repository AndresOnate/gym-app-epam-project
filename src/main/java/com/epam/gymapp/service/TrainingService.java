package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.model.trainingType.TrainingTypeEnum;
import com.epam.gymapp.repository.TraineeRepository;
import com.epam.gymapp.repository.TrainerRepository;
import com.epam.gymapp.repository.TrainingRepository;
import com.epam.gymapp.repository.TrainingTypeRepository;

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
    private TrainerRepository trainerRepository; // Field-Based Injection
    private TraineeRepository traineeRepository; // Field-Based Injection
    private TrainingTypeRepository trainingTypeRepository; // Field-Based Injection

    public TrainingService(TrainingRepository trainingRepository, TrainerRepository trainerRepository,
            TraineeRepository traineeRepository, TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
    }

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

    public Training save(TrainingDto trainingDto) {
        logger.info("Saving new training: {}", trainingDto);
        Training training = new Training();

        TrainingTypeEnum typeEnum = TrainingTypeEnum.valueOf(trainingDto.getTrainingTypeName().toUpperCase());
        TrainingType trainingType = trainingTypeRepository.findByName(typeEnum)
                                  .orElseThrow(() -> new RuntimeException("Tipo de entrenamiento no encontrado"));

        Trainer trainer = trainerRepository.findByUserUsername(trainingDto.getTrainerUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with username: " + trainingDto.getTrainerUsername()));
        Trainee trainee = traineeRepository.findByUserUsername(trainingDto.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found with username: " + trainingDto.getTraineeUsername()));
        
        training.setTrainingType(trainingType);
        training.setTrainee(trainee);
        training.setTrainer(trainer);        
        training.setTrainingName(trainingDto.getTrainingName());
        training.setTrainingDate(trainingDto.getTrainingDate());
        training.setTrainingDuration(trainingDto.getTrainingDuration());
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

    /**
     * Retrieves trainings for a specific trainee based on filters.
     */
    public List<TrainingDto> getTraineeTrainings(String traineeUsername, Date fromDate, Date toDate, 
                                              String trainerName, String trainingType) {
        logger.info("Fetching trainings for trainee: {}, from: {}, to: {}, trainer: {}, type: {}",
                traineeUsername, fromDate, toDate, trainerName, trainingType);
        
        List<Training> trainings = trainingRepository.findByTraineeUserUsernameAndTrainingDateBetweenAndTrainerUserUsernameContainingAndTrainingTypeNameContaining(
                traineeUsername, fromDate, toDate, trainerName, trainingType);

        return trainings.stream().map(training -> {
            TrainingDto trainingDto = new TrainingDto();
            trainingDto.setTrainingName(training.getTrainingName());
            trainingDto.setTrainingDate(training.getTrainingDate());
            trainingDto.setTrainingType(training.getTrainingType().getName().toString());
            trainingDto.setTrainingDuration(training.getTrainingDuration());
            return trainingDto;
        }).toList();
    }

    /**
     * Retrieves trainings for a specific trainer based on filters.
     */
    public List<TrainingDto> getTrainerTrainings(String trainerUsername, Date fromDate, Date toDate, 
                                              String traineeName) {
        logger.info("Fetching trainings for trainer: {}, from: {}, to: {}, trainee: {}",
                trainerUsername, fromDate, toDate, traineeName);
        
        List<Training> trainings = trainingRepository.findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserUsernameContaining(
                trainerUsername, fromDate, toDate, traineeName);

        return trainings.stream().map(training -> {
            TrainingDto trainingDto = new TrainingDto();
            trainingDto.setTrainingName(training.getTrainingName());
            trainingDto.setTrainingDate(training.getTrainingDate());
            trainingDto.setTrainingType(training.getTrainingType().getName().toString());
            trainingDto.setTrainingDuration(training.getTrainingDuration());
            return trainingDto;
        }).toList();
    }
}