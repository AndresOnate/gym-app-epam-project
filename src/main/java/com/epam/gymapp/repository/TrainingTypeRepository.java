package com.epam.gymapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.model.trainingType.TrainingTypeEnum;


/**
 * Repository interface for managing rainingType entities.
 * This interface extends JpaRepository, providing built-in methods for 
 * CRUD operations (Create, Read, Update, Delete) on TrainingType objects.
 * It is annotated with @Repository to be recognized as a Spring Bean 
 * and automatically injected where needed.
 */
@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
    Optional<TrainingType> findByName(TrainingTypeEnum name);

}