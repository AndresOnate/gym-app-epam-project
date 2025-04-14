package com.epam.gymapp.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.epam.gymapp.model.trainer.Trainer;


/**
 * Repository interface for managing Trainer entities.
 * This interface extends JpaRepository, providing built-in methods for 
 * CRUD operations (Create, Read, Update, Delete) on Trainer objects.
 * It is annotated with @Repository to be recognized as a Spring Bean 
 * and automatically injected where needed.
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    /**
     * Finds a Trainer by the username of the associated User.
     *
     * @param username The username to search for.
     * @return The Trainer object if found, or an empty Optional if not found.
     */
    Optional<Trainer> findByUserUsername(String username);

    /**
     * Find all trainers assigned to a specific trainee.
     */
    @Query("SELECT DISTINCT t.trainer FROM Training t WHERE t.trainee.user.username = :traineeUsername")
    List<Trainer> findAssignedTrainersByTraineeUsername(@Param("traineeUsername") String traineeUsername);

    List<Trainer> findByUserUsernameIn(List<String> usernames);


}