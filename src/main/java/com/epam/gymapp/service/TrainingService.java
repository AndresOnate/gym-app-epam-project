package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.epam.gymapp.client.TrainerWorkloadClient;
import com.epam.gymapp.dto.ActionType;
import com.epam.gymapp.dto.TrainerWorkloadRequest;
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
    private TrainingRepository trainingRepository; 
    private TrainerRepository trainerRepository; 
    private TraineeRepository traineeRepository;
    private TrainingTypeRepository trainingTypeRepository; 
    private TrainerWorkloadClient trainerWorkloadClient;
    private TrainingPublisher trainingPublisher;

    public TrainingService(TrainingRepository trainingRepository, TrainerRepository trainerRepository,
            TraineeRepository traineeRepository, TrainingTypeRepository trainingTypeRepository, TrainerWorkloadClient trainerWorkloadClient, TrainingPublisher trainingPublisher) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainerWorkloadClient = trainerWorkloadClient;
        this.trainingPublisher = trainingPublisher;
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
        Training saved = trainingRepository.save(training);
        LocalDate localDate = trainingDto.getTrainingDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
        TrainerWorkloadRequest apiRequest = new TrainerWorkloadRequest();
        apiRequest.setUsername(trainer.getUser().getUsername());
        apiRequest.setFirstName(trainer.getUser().getFirstName());
        apiRequest.setLastName(trainer.getUser().getLastName());
        apiRequest.setIsActive(trainer.getUser().getIsActive());
        apiRequest.setTrainingDate(localDate);
        apiRequest.setTrainingDuration(trainingDto.getTrainingDuration());
        apiRequest.setActionType(ActionType.ADD);
        logger.info("Notifying the secondary microservice: {}", apiRequest.toString());


        trainingPublisher.sendTraining(apiRequest);
        

        return saved;
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
        trainingRepository.findById(id).ifPresent(training -> {
             logger.info("Deleting training: {}", id);
            trainingRepository.deleteById(id);

            Trainer trainer = training.getTrainer();

            TrainerWorkloadRequest request = new TrainerWorkloadRequest();
            request.setUsername(trainer.getUser().getUsername());
            request.setFirstName(trainer.getUser().getFirstName());
            request.setLastName(trainer.getUser().getLastName());
            request.setIsActive(trainer.getUser().getIsActive());
            request.setTrainingDate(training.getTrainingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            request.setTrainingDuration(training.getTrainingDuration());
            request.setActionType(ActionType.DELETE);

            logger.info("Notifying the secondary microservice: {}", request.toString());

            trainerWorkloadClient.updateTrainerWorkload(request);
        });
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