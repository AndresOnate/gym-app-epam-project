package com.epam.gymapp.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.storage.TrainingStorage;

/**
 * Data Access Object (DAO) implementation for managing Training entities.
 * This class provides methods for CRUD operations (Create, Read, Update, Delete)
 * on Training objects, and uses an in-memory HashMap for storage.
 * 
 * The class is annotated with @Repository to be recognized as a Spring Bean
 * and automatically injected into service classes.
 */
@Repository
public class TrainingDao implements Dao<Training> {

    private final TrainingStorage trainingStorage;

    @Autowired
    public TrainingDao(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Optional<Training> get(Long id) {
        return Optional.ofNullable(trainingStorage.getStorage().get(id));
    }

    @Override
    public Optional<Training> getById(Long id) {
        return Optional.ofNullable(trainingStorage.getStorage().get(id));
    }

    @Override
    public List<Training> getAll() {
        return List.copyOf(trainingStorage.getStorage().values());
    }

    @Override
    public Training save(Training training) {
        if (training == null || training.getId() == null) {
            throw new IllegalArgumentException("Training or ID cannot be null.");
        }
        trainingStorage.getStorage().put(training.getId(), training);  
        return training;
    }

    @Override
    public Training update(Long id, Training updatedTraining) {
        if (id == null || updatedTraining == null) {
            throw new IllegalArgumentException("ID and Training cannot be null.");
        }
        if (!trainingStorage.getStorage().containsKey(id)) {
            throw new IllegalArgumentException("Training not found.");
        }
        updatedTraining.setId(id);
        trainingStorage.getStorage().put(id, updatedTraining);  
        return updatedTraining;
    }

    @Override
    public void delete(Long id) {
        trainingStorage.getStorage().remove(id);
    }
}