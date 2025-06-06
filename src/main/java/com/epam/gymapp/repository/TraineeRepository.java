package com.epam.gymapp.repository;


import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.epam.gymapp.model.trainee.Trainee;


/**
 * Repository interface for managing Trainee entities.
 * This interface extends JpaRepository, providing built-in methods for 
 * CRUD operations (Create, Read, Update, Delete) on Trainee objects.
 * It is annotated with @Repository to be recognized as a Spring Bean 
 * and automatically injected where needed.
 */
@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    /**
     * Finds a Trainee by the username of the associated User.
     *
     * @param username The username to search for.
     * @return The Trainee object if found, or an empty Optional if not found.
     */
    Optional<Trainee> findByUserUsername(String username);
    
}

