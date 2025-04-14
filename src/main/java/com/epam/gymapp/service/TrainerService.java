package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.epam.gymapp.dto.RegistrationDto;
import com.epam.gymapp.dto.TraineeDto;
import com.epam.gymapp.dto.TraineeProfileDto;
import com.epam.gymapp.dto.TrainerDto;
import com.epam.gymapp.dto.TrainerProfileDto;
import com.epam.gymapp.exception.NotFoundException;
import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.model.trainingType.TrainingTypeEnum;
import com.epam.gymapp.model.user.User;
import com.epam.gymapp.repository.TrainerRepository;
import com.epam.gymapp.repository.TrainingTypeRepository;
import com.epam.gymapp.repository.UserRepository;

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
    private final UserRepository userRepository; // Field-Based Injection
    private final TrainingTypeRepository trainingTypeRepository; // Field-Based Injection

    public TrainerService(TrainerRepository trainerRepository, UserRepository userRepository, TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.userRepository = userRepository;
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
    public RegistrationDto save(TrainerDto trainerDto) {
        TrainingTypeEnum typeEnum = TrainingTypeEnum.valueOf(trainerDto.getSpecialization().toUpperCase());
        TrainingType trainingType = trainingTypeRepository.findByName(typeEnum)
                                  .orElseThrow(() -> new RuntimeException("Tipo de entrenamiento no encontrado"));
        Trainer trainer = new Trainer(trainerDto);
        User user = trainer.getUser();
        userRepository.save(user);
        logger.info("Saving new trainer: {}", trainer);
        trainer.setSpecialization(trainingType);
        trainer = trainerRepository.save(trainer);
        return new RegistrationDto(user.getUsername(), user.getPassword());
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

    /**
    * Finds a Trainer by their username.
    *
    * @param username The username to search for.
    * @return The Trainer object if found, or an empty Optional if not found.
    */
   public Optional<Trainer> findByUsername(String username) {
       logger.info("Searching for trainer with username: {}", username);
       Optional<Trainer> trainer = trainerRepository.findByUserUsername(username);
       if (trainer.isPresent()) {
           logger.info("Trainer with username '{}' found.", username);
       } else {
           logger.warn("Trainer with username '{}' not found.", username);
       }
       return trainer;
   }

    /**
     * Get trainers that are NOT assigned to a specific trainee.
     */
    public List<TrainerDto> getUnassignedTrainers(String traineeUsername) {
        logger.info("Fetching unassigned trainers for trainee: {}", traineeUsername);
        List<Trainer> allTrainers = trainerRepository.findAll();
        List<Trainer> assignedTrainers = trainerRepository.findAssignedTrainersByTraineeUsername(traineeUsername);
        allTrainers.removeAll(assignedTrainers); 
        List<TrainerDto> allTrainersDto = allTrainers.stream()
            .map(trainer -> {
                TrainerDto dto = new TrainerDto();
                dto.setUsername(trainer.getUser().getUsername());
                dto.setFirstName(trainer.getUser().getFirstName());
                dto.setLastName(trainer.getUser().getLastName());
                dto.setSpecialization(trainer.getSpecialization().getName().toString());
                return dto;
            })
            .collect(Collectors.toList());
        return allTrainersDto;


    }

    /**
     * Retrieves the profile of a trainer by their username.
     * 
     * @param username the username of the trainer
     * @return the TrainerProfileDto object containing the trainer's profile information
     */
    public TrainerProfileDto getTrainerProfileByUsername(String username) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUserUsername(username);
        if (trainerOptional.isEmpty()) {
            throw new NotFoundException("Trainer not found with username: " + username);
        }
        Trainer trainer = trainerOptional.get();
        TrainerProfileDto dto = new TrainerProfileDto();
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setSpecialization(trainer.getSpecialization().getName().toString());
        dto.setIsActive(trainer.getUser().getIsActive());

        List<TraineeDto> trainees = trainer.getTrainees().stream()
            .map(t -> {
                TraineeDto tsd = new TraineeDto();
                tsd.setUsername(t.getUser().getUsername());
                tsd.setFirstName(t.getUser().getFirstName());
                tsd.setLastName(t.getUser().getLastName());
                return tsd;
            })
            .collect(Collectors.toList());

        dto.setTrainees(trainees);
        return dto;
    }

    
    /**
     * Updates the trainer's profile information.
     * 
     * @param username the username of the trainer to update
     * @param requestDto the updated profile information
     * @return the updated TrainerProfileDto object
     */
    public TrainerProfileDto updateTrainerProfile(String username, TrainerProfileDto requestDto) {
        Trainer trainer = trainerRepository.findByUserUsername(username)
            .orElseThrow(() -> new NotFoundException("Trainer not found with username: " + username));

        trainer.getUser().setFirstName(requestDto.getFirstName());
        trainer.getUser().setLastName(requestDto.getLastName());
        trainer.getUser().setIsActive(requestDto.getIsActive());

        trainerRepository.save(trainer);


        TrainerProfileDto dto = new TrainerProfileDto();
        dto.setUsername(trainer.getUser().getUsername());
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setSpecialization(trainer.getSpecialization().getName().toString());
        dto.setIsActive(trainer.getUser().getIsActive());

        List<TraineeDto> trainees = trainer.getTrainees().stream()
            .map(t -> {
                TraineeDto tsd = new TraineeDto();
                tsd.setUsername(t.getUser().getUsername());
                tsd.setFirstName(t.getUser().getFirstName());
                tsd.setLastName(t.getUser().getLastName());
                return tsd;
            })
            .collect(Collectors.toList());

        dto.setTrainees(trainees);
        return dto;
    }

    /**
     * Updates the status of a trainer (active/inactive).
     * 
     * @param username the username of the trainer to update
     * @param isActive the new status (true for active, false for inactive)
     */
    public void updateStatus(String username, boolean isActive) {
        Trainer trainer = trainerRepository.findByUserUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Aprendiz no encontrado con username: " + username));
        trainer.getUser().setActive(isActive);
        trainerRepository.save(trainer);
    }
}