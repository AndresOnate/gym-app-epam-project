package com.epam.gymapp.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.storage.TrainerStorage;

/**
 * Data Access Object (DAO) implementation for managing Trainer entities.
 * This class provides methods for CRUD operations (Create, Read, Update, Delete)
 * on Trainer objects, and uses an in-memory HashMap for storage.
 * 
 * The class is annotated with @Repository to be recognized as a Spring Bean
 * and automatically injected into service classes.
 */
@Repository
public class TrainerDao implements Dao<Trainer> {

    private final TrainerStorage trainerStorage;

    @Autowired
    public TrainerDao(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Optional<Trainer> get(Long id) {
        return Optional.ofNullable(trainerStorage.getStorage().get(id));
    }

    @Override
    public Optional<Trainer> getById(Long id) {
        return Optional.ofNullable(trainerStorage.getStorage().get(id));
    }

    @Override
    public List<Trainer> getAll() {
        return List.copyOf(trainerStorage.getStorage().values());
    }

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null || trainer.getId() == null) {
            throw new IllegalArgumentException("Trainer or ID cannot be null.");
        }
        trainerStorage.getStorage().put(trainer.getId(), trainer);  
        return trainer;
    }

    @Override
    public Trainer update(Long id, Trainer updatedTrainer) {
        if (id == null || updatedTrainer == null) {
            throw new IllegalArgumentException("ID and Trainer cannot be null.");
        }
        if (!trainerStorage.getStorage().containsKey(id)) {
            throw new IllegalArgumentException("Trainer not found.");
        }
        updatedTrainer.setId(id);
        trainerStorage.getStorage().put(id, updatedTrainer);  
        return updatedTrainer;
    }

    @Override
    public void delete(Long id) {
        trainerStorage.getStorage().remove(id); 
    }
}