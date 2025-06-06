package com.epam.gymapp.repository;


import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epam.gymapp.model.training.Training;


/**
 * Repository interface for managing Training entities.
 * This interface extends JpaRepository, providing built-in methods for 
 * CRUD operations (Create, Read, Update, Delete) on Trainer objects.
 * It is annotated with @Repository to be recognized as a Spring Bean 
 * and automatically injected where needed.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByTraineeUserUsernameAndTrainingDateBetweenAndTrainerUserUsernameContainingAndTrainingTypeNameContaining(
        String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType
    );

    List<Training> findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserUsernameContaining(
        String trainerUsername, Date fromDate, Date toDate, String traineeName
    );
}