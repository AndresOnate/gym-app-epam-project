package com.epam.gymapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.storage.TraineeStorage;

/**
 * Data Access Object (DAO) implementation for managing Trainee entities.
 * This class provides methods for CRUD operations (Create, Read, Update, Delete)
 * on Trainee objects, and uses an in-memory HashMap for storage.
 * 
 * The class is annotated with @Repository to be recognized as a Spring Bean
 * and automatically injected into service classes.
 */
@Repository
public class TraineeDao implements Dao<Trainee> {

    private final TraineeStorage traineeStorage;

    @Autowired
    public TraineeDao(TraineeStorage traineeStorage){
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Optional<Trainee> get(Long id) {
        return Optional.ofNullable(traineeStorage.getTrainees().get(id));
    }

    @Override
    public List<Trainee> getAll() {
        return List.copyOf(traineeStorage.getTrainees().values());
    }

    @Override
    public Optional<Trainee> getById(Long id) {
        return Optional.ofNullable(traineeStorage.getTrainees().get(id));
    }

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee == null || trainee.getId() == null) {
            throw new IllegalArgumentException("Trainee or ID cannot be null.");
        }
        traineeStorage.getTrainees().put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public Trainee update(Long id, Trainee updatedTrainee) {
        if (id == null || updatedTrainee == null) {
            throw new IllegalArgumentException("ID and Trainee cannot be null.");
        }
        if (!traineeStorage.getTrainees().containsKey(id)) {
            throw new IllegalArgumentException("Trainee not found.");
        }
        updatedTrainee.setId(id);
        traineeStorage.getTrainees().put(id, updatedTrainee);
        return updatedTrainee;
    }

    @Override
    public void delete(Long id) {
        traineeStorage.getTrainees().remove(id);
    }
}

