package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.ZoneId;
import java.util.List;

import com.epam.gymapp.config.JwtUtils;
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
import com.epam.gymapp.util.TransactionContext;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.web.reactive.function.client.WebClient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

/**
 * Service layer for managing operations related to the Training entity.
 * This service class provides methods to interact with the underlying data
 * repository (TrainingDao) for creating, retrieving, updating, and deleting
 * Training objects.
 */
@Service
public class TrainingService {

    private static final Logger transactionLogger = LoggerFactory.getLogger("transactionLogger");
    private static final Logger operationLogger = LoggerFactory.getLogger("operationLogger");

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final WebClient.Builder loadBalancedWebClientBuilder; 
    private final JwtUtils jwtService; 

    private static final String TRAINER_WORKLOAD_SERVICE_NAME = "TRAINER-WORKLOAD-SERVICE"; 

    public TrainingService(TrainingRepository trainingRepository,
                           TrainerRepository trainerRepository,
                           TraineeRepository traineeRepository,
                           TrainingTypeRepository trainingTypeRepository,
                           WebClient.Builder loadBalancedWebClientBuilder, 
                           JwtUtils jwtService) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
        this.jwtService = jwtService;
    }

    // --- Existing methods (adjust logging as needed) ---

    @Transactional(readOnly = true)
    public List<Training> getAll() {
        String transactionId = TransactionContext.getTransactionId();
        operationLogger.info("[{}] Fetching all trainings", transactionId);
        return trainingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Training getById(Long id) {
        String transactionId = TransactionContext.getTransactionId();
        operationLogger.info("[{}] Fetching training with ID: {}", transactionId, id);
        return trainingRepository.findById(id).orElse(null);
    }

    // --- Core Updates for save and delete methods ---

    /**
     * Saves a new training session to the database and notifies the workload service.
     * @param trainingDto The TrainingDto object to save
     * @return The saved Training object
     */
    @Transactional
    public Training save(TrainingDto trainingDto) {
        String transactionId = TransactionContext.getTransactionId();
        transactionLogger.info("[{}] Transaction: Saving new training started. Request: {}", transactionId, trainingDto.toString());
        operationLogger.info("[{}] Main service: Preparing to save new training for trainer: {}", transactionId, trainingDto.getTrainerUsername());

        Training training = new Training();

        TrainingTypeEnum typeEnum = TrainingTypeEnum.valueOf(trainingDto.getTrainingTypeName().toUpperCase());
        TrainingType trainingType = trainingTypeRepository.findByName(typeEnum)
                                     .orElseThrow(() -> new EntityNotFoundException("Training type not found: " + trainingDto.getTrainingTypeName()));

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

        Training savedTraining = trainingRepository.save(training);
        operationLogger.info("[{}] Main service: Training session saved in local DB with ID: {}", transactionId, savedTraining.getId());

        // Call the secondary microservice to update trainer workload asynchronously
        callTrainerWorkloadService(savedTraining, ActionType.ADD, transactionId)
                .subscribe(
                    success -> operationLogger.info("[{}] Main service: Workload update call successful for trainer {}", transactionId, savedTraining.getTrainer().getUser().getUsername()),
                    error -> operationLogger.error("[{}] Main service: Workload update call failed for trainer {}: {}", transactionId, savedTraining.getTrainer().getUser().getUsername(), error.getMessage())
                );
        // Using .subscribe() makes the call non-blocking. This means the main service's
        // response won't wait for the workload service. This is generally preferred for resilience
        // and performance, relying on the fallback/retry mechanism for eventual consistency.

        transactionLogger.info("[{}] Transaction: Saving new training completed successfully. Response: 200 OK, Session ID: {}", transactionId, savedTraining.getId());
        return savedTraining;
    }


    /**
     * Deletes a training from the database and notifies the workload service.
     * @param id the ID of the training to delete
     */
    @Transactional
    public void delete(Long id) {
        String transactionId = TransactionContext.getTransactionId();
        transactionLogger.info("[{}] Transaction: Deleting training started. Request ID: {}", transactionId, id);
        operationLogger.info("[{}] Main service: Attempting to delete training session with ID: {}", transactionId, id);

        Training trainingToDelete = trainingRepository.findById(id)
                .orElseThrow(() -> {
                    transactionLogger.error("[{}] Transaction: Deleting training failed. Error: Training not found for ID {}", transactionId, id);
                    return new EntityNotFoundException("Training not found with ID: " + id);
                });

        trainingRepository.delete(trainingToDelete);
        operationLogger.info("[{}] Main service: Training session deleted from local DB: {}", transactionId, id);

        // Call the secondary microservice to update trainer workload asynchronously
        callTrainerWorkloadService(trainingToDelete, ActionType.DELETE, transactionId)
                .subscribe(
                    success -> operationLogger.info("[{}] Main service: Workload update call successful for trainer {}", transactionId, trainingToDelete.getTrainer().getUser().getUsername()),
                    error -> operationLogger.error("[{}] Main service: Workload update call failed for trainer {}: {}", transactionId, trainingToDelete.getTrainer().getUser().getUsername(), error.getMessage())
                );

        transactionLogger.info("[{}] Transaction: Deleting training completed successfully. Response: 200 OK.", transactionId);
    }

    // --- Existing update and retrieval methods (adjust logging) ---

    @Transactional
    public Training update(Long id, Training updatedTraining) {
        String transactionId = TransactionContext.getTransactionId();
        operationLogger.info("[{}] Updating training with ID: {}", transactionId, id);

        return trainingRepository.findById(id)
                .map(existingTraining -> {
                    existingTraining.setTrainingName(updatedTraining.getTrainingName());
                    existingTraining.setTrainingDuration(updatedTraining.getTrainingDuration());
                    existingTraining.setTrainingDate(updatedTraining.getTrainingDate());
                    existingTraining.setTrainingType(updatedTraining.getTrainingType());
                    Training savedTraining = trainingRepository.save(existingTraining);
                    operationLogger.info("[{}] Training with ID: {} successfully updated", transactionId, id);
                    return savedTraining;
                })
                .orElseThrow(() -> {
                    operationLogger.error("[{}] Training with ID: {} not found for update", transactionId, id);
                    return new EntityNotFoundException("Training not found with ID: " + id);
                });
    }

    @Transactional(readOnly = true)
    public List<TrainingDto> getTraineeTrainings(String traineeUsername, Date fromDate, Date toDate,
                                                  String trainerName, String trainingType) {
        String transactionId = TransactionContext.getTransactionId();
        operationLogger.info("[{}] Fetching trainings for trainee: {}, from: {}, to: {}, trainer: {}, type: {}",
                transactionId, traineeUsername, fromDate, toDate, trainerName, trainingType);

        List<Training> trainings = trainingRepository.findByTraineeUserUsernameAndTrainingDateBetweenAndTrainerUserUsernameContainingAndTrainingTypeNameContaining(
                traineeUsername, fromDate, toDate, trainerName, trainingType);

        return trainings.stream().map(training -> {
            TrainingDto trainingDto = new TrainingDto();
            trainingDto.setTrainingName(training.getTrainingName());
            trainingDto.setTrainingDate(training.getTrainingDate());
            trainingDto.setTrainingType(training.getTrainingType().getName().toString());
            trainingDto.setTrainingDuration(training.getTrainingDuration());
            trainingDto.setTraineeUsername(training.getTrainee().getUser().getUsername()); // Ensure DTO has this
            trainingDto.setTrainerUsername(training.getTrainer().getUser().getUsername()); // Ensure DTO has this
            return trainingDto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingDto> getTrainerTrainings(String trainerUsername, Date fromDate, Date toDate,
                                                  String traineeName) {
        String transactionId = TransactionContext.getTransactionId();
        operationLogger.info("[{}] Fetching trainings for trainer: {}, from: {}, to: {}, trainee: {}",
                transactionId, trainerUsername, fromDate, toDate, traineeName);

        List<Training> trainings = trainingRepository.findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserUsernameContaining(
                trainerUsername, fromDate, toDate, traineeName);

        return trainings.stream().map(training -> {
            TrainingDto trainingDto = new TrainingDto();
            trainingDto.setTrainingName(training.getTrainingName());
            trainingDto.setTrainingDate(training.getTrainingDate());
            trainingDto.setTrainingType(training.getTrainingType().getName().toString());
            trainingDto.setTrainingDuration(training.getTrainingDuration());
            trainingDto.setTraineeUsername(training.getTrainee().getUser().getUsername()); // Ensure DTO has this
            trainingDto.setTrainerUsername(training.getTrainer().getUser().getUsername()); // Ensure DTO has this
            return trainingDto;
        }).toList();
    }

    /**
     * Calls the Trainer Workload Microservice with Circuit Breaker protection.
     * Uses WebClient with Eureka for service discovery.
     * Returns Mono<Void> for reactive flow.
     */
    @CircuitBreaker(name = "trainerWorkloadService", fallbackMethod = "fallbackForTrainerWorkloadService")
    private Mono<Void> callTrainerWorkloadService(Training training, ActionType actionType, String transactionId) {
        operationLogger.info("[{}] Main service: Preparing to call Trainer Workload Service (via Eureka) for trainer {} with action {}",
                transactionId, training.getTrainer().getUser().getUsername(), actionType);


        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
            training.getTrainer().getUser().getUsername(),
            training.getTrainer().getUser().getFirstName(),
            training.getTrainer().getUser().getLastName(),
            training.getTrainer().getUser().getIsActive(), 
            training.getTrainingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 
            training.getTrainingDuration(),
            actionType
        );

        String jwtToken = jwtService.generateToken("main-microservice", transactionId); // Generate JWT token

        // Use the load-balanced WebClient.Builder and specify the service name
        return loadBalancedWebClientBuilder.build()
                .post()
                .uri(String.format("http://%s/api/workload/update", TRAINER_WORKLOAD_SERVICE_NAME))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .header("X-Transaction-ID", transactionId) // Propagate transaction ID
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                          clientResponse -> clientResponse.bodyToMono(String.class)
                                  .flatMap(errorBody -> {
                                      operationLogger.error("[{}] Error from Trainer Workload Service (Status: {}): {}",
                                              transactionId, clientResponse.statusCode(), errorBody);
                                      return Mono.error(new RuntimeException("Trainer Workload Service error: " + errorBody));
                                  })
                )
                .toBodilessEntity() // We expect no response body, just status
                .then(); // Convert to Mono<Void>
    }

    /**
     * Fallback method for the trainerWorkloadService circuit breaker.
     * This method will be called if the call to the secondary service fails or the circuit is open.
     */
    private Mono<Void> fallbackForTrainerWorkloadService(Training training, ActionType actionType, String transactionId, Throwable t) {
        transactionLogger.error("[{}] Circuit Breaker Fallback: Trainer Workload Service call failed for trainer {} (action: {}). Error: {}",
                transactionId, training.getTrainer().getUser().getUsername(), actionType, t.getMessage());
        operationLogger.warn("[{}] Fallback triggered for trainerWorkloadService. Implement robust error handling or retry mechanism here.", transactionId);
        return Mono.empty(); 
    }
}