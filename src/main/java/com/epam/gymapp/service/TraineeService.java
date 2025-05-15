package com.epam.gymapp.service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.epam.gymapp.dto.TraineeDto;
import com.epam.gymapp.dto.TraineeProfileDto;
import com.epam.gymapp.dto.TrainerDto;
import com.epam.gymapp.exception.NotFoundException;
import com.epam.gymapp.dto.RegistrationDto;
import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.user.User;
import com.epam.gymapp.repository.TraineeRepository;
import com.epam.gymapp.repository.TrainerRepository;
import com.epam.gymapp.repository.TrainingTypeRepository;
import com.epam.gymapp.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

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
    private TraineeRepository traineeRepository;
    private UserRepository userRepository;
    private TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    public TraineeService(TraineeRepository traineeRepository, UserRepository userRepository, TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Retrieves all trainees from the database.
     * 
     * @return a list of all trainees
     */
    public List<Trainee> getAll() {
        logger.info("Fetching all trainees...");
        return traineeRepository.findAll();
    }

    /**
     * Retrieves a trainee by their ID.
     * 
     * @param id the ID of the trainee to retrieve
     * @return the Trainee object with the given ID, or null if not found
     */
    public Trainee getById(Long id) {
        logger.info("Fetching trainee with ID: {}", id);
        return traineeRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new trainee to the database.
     * This method persists a new Trainee object using the DAO.
     * 
     * @param trainee the Trainee object to save
     * @return the saved Trainee object
     */
    public RegistrationDto save(TraineeDto traineeDto) {
        Trainee trainee = new Trainee(traineeDto);
        User user = trainee.getUser();
        String generatedPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(trainee.getUser()); 
        logger.info("Saving new trainee: {}", trainee);
        trainee = traineeRepository.save(trainee);
        return new RegistrationDto(user.getUsername(), generatedPassword);
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
        return traineeRepository.findById(id)
        .map(existingTrainee -> {
            existingTrainee.setAddress(updatedTrainee.getAddress());
            Trainee savedTrainee = traineeRepository.save(existingTrainee);
            logger.info("Trainee with ID: {} successfully updated", id);
            return savedTrainee;
        })
        .orElseThrow(() -> {
            logger.error("Trainee with ID: {} not found", id);
            return new EntityNotFoundException("Trainee not found with ID: " + id);
        });
    }

    /**
     * Deletes a trainee from the database.
     * 
     * @param trainee the Trainee object to delete
     */
    public void deleteTrainee(Long traineeId) {
        Optional<Trainee> traineeOptional = traineeRepository.findById(traineeId);
        if (traineeOptional.isPresent()) {
            Trainee trainee = traineeOptional.get();
            traineeRepository.delete(trainee); 
            logger.info("Trainee with ID {} deleted successfully.", traineeId);
        } else {
            throw new RuntimeException("Trainee not found with ID " + traineeId);
        }
    }

        /**
     * Finds a Trainee by their username.
     *
     * @param username The username to search for.
     * @return The Trainee object if found, or an empty Optional if not found.
     */
    public Optional<Trainee> findByUsername(String username) {
        logger.info("Searching for trainee with username: {}", username);
        Optional<Trainee> trainee = traineeRepository.findByUserUsername(username);
        if (trainee.isPresent()) {
            logger.info("Trainee with username '{}' found.", username);
        } else {
            logger.warn("Trainee with username '{}' not found.", username);
        }
        return trainee;
    }

    /**
     * Updates the list of trainers for a trainee.
     *
     * @param traineeUsername The username of the trainee.
     * @param trainers The new list of trainers to assign.
     */
    public void updateTraineeTrainers(String traineeUsername, Set<Trainer> trainers) {
        logger.info("Updating trainers for trainee: {}", traineeUsername);
        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
        trainee.setTrainers(trainers);
        traineeRepository.save(trainee);
    }

    /**
     * Updates the list of trainers for a trainee.
     *
     * @param traineeUsername The username of the trainee.
     * @param trainerUsernames The list of trainer usernames to assign.
     * @return A list of TrainerDto objects representing the updated trainers.
     */
    public List<TrainerDto> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new NotFoundException("Trainee not found with username: " + traineeUsername));
        List<Trainer> trainers = trainerRepository.findByUserUsernameIn(trainerUsernames);
        if (trainers.size() != trainerUsernames.size()) {
            throw new NotFoundException("One or more trainers not found.");
        }
        trainee.setTrainers(new HashSet<>(trainers));
        traineeRepository.save(trainee);
        return trainers.stream()
                .map(trainer -> {
                        TrainerDto trainerDto =  new TrainerDto();
                        trainerDto.setUsername(trainer.getUser().getUsername());
                        trainerDto.setFirstName(trainer.getUser().getFirstName());
                        trainerDto.setLastName(trainer.getUser().getLastName());
                        trainerDto.setSpecialization(
                            trainer.getSpecialization() != null && trainer.getSpecialization().getName() != null
                                ? trainer.getSpecialization().getName().toString()
                                : null
                        );  
                        return trainerDto;})
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the profile of a trainee by their username.
     *
     * @param username The username of the trainee.
     * @return The TraineeProfileDto object containing the trainee's profile information.
     * @throws NotFoundException if the trainee is not found.
     */
    public TraineeProfileDto getProfileByUsername(String username) {
        Trainee trainee = traineeRepository.findByUserUsername(username)
            .orElseThrow(() -> new NotFoundException("Trainee not found with username: " + username));
        
        User user = trainee.getUser();
        if (user == null) {
            throw new NotFoundException("User information missing for trainee with username: " + username);
        }
    
        TraineeProfileDto profile = new TraineeProfileDto();
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setDateOfBirth(trainee.getDateOfBirth() != null ? trainee.getDateOfBirth().toLocalDate() : null);
        profile.setAddress(trainee.getAddress());
        profile.setActive(Boolean.TRUE.equals(user.getIsActive())); // más seguro para Boolean
    
        List<TrainerDto> trainers = trainee.getTrainers().stream().map(trainer -> {
            TrainerDto dto = new TrainerDto();
            User trainerUser = trainer.getUser();
            dto.setUsername(trainerUser != null ? trainerUser.getUsername() : null);
            dto.setFirstName(trainerUser != null ? trainerUser.getFirstName() : null);
            dto.setLastName(trainerUser != null ? trainerUser.getLastName() : null);
            
            dto.setSpecialization(
                trainer.getSpecialization() != null && trainer.getSpecialization().getName() != null
                    ? trainer.getSpecialization().getName().toString()
                    : null
            );
            return dto;
        }).collect(Collectors.toList());
    
        profile.setTrainers(trainers);
        return profile;
    }


    /**
     * Updates the profile of a trainee.
     *
     * @param username The username of the trainee to update.
     * @param request The updated profile information.
     * @return The updated TraineeProfileDto object.
     * @throws NotFoundException if the trainee is not found.
     */
    public TraineeProfileDto updateProfile(String username, TraineeProfileDto request) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUserUsername(username);
        if (traineeOptional.isEmpty()) {
            throw new NotFoundException("Trainee not found with username: " + username);
        }
        Trainee trainee = traineeOptional.get();
        User user = trainee.getUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        trainee.setAddress(request.getAddress());
        user.setIsActive(request.isActive());
        traineeRepository.save(trainee);
        List<TrainerDto> trainers = trainee.getTrainers().stream().map(trainer -> {
            TrainerDto dto = new TrainerDto();
            User trainerUser = trainer.getUser();
            dto.setUsername(trainerUser.getUsername());
            dto.setFirstName(trainerUser.getFirstName());
            dto.setLastName(trainerUser.getLastName());
            dto.setSpecialization(trainer.getSpecialization().getName().toString());
            return dto;
        }).collect(Collectors.toList());
        request.setTrainers(trainers);
        return request;
    }

    /**
     * Deletes a trainee by their username.
     *
     * @param username The username of the trainee to delete.
     * @throws NotFoundException if the trainee is not found.
     */
    public void deleteTraineeByUsername(String username) {
        logger.info("Deleting trainee with username: {}", username);
        Optional<Trainee> traineeOptional = traineeRepository.findByUserUsername(username);
        if (traineeOptional.isEmpty()) {
            throw new NotFoundException("Trainee not found with username: " + username);
        }
        Trainee trainee = traineeOptional.get();
        traineeRepository.delete(trainee);
        logger.info("Trainee with username {} deleted successfully.", username);
    }

    /**
     * Updates the status of a trainee (active/inactive).
     *
     * @param username The username of the trainee.
     * @param isActive The new status to set (true for active, false for inactive).
     */
    public void updateStatus(String username, boolean isActive) {
        Trainee trainee = traineeRepository.findByUserUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Aprendiz no encontrado con username: " + username));
        trainee.getUser().setActive(isActive);
        traineeRepository.save(trainee);
    }
    
}
