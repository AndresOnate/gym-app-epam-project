package com.epam.gymapp.storage;

import com.epam.gymapp.model.trainee.Trainee;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Storage component for managing trainees.
 * Loads initial data from a file during application startup.
 */
@Component
public class TraineeStorage extends BaseStorage<Trainee> {

    @Value("${storage.trainee.file.path}")
    private String filePath;

    public TraineeStorage() {
        super(new HashMap<>());
    }

    @PostConstruct
    private void init() {
        loadDataFromFile(filePath, this::parseTrainee);
    }

    public Map<Long, Trainee> getTrainees() {
        return storage;
    }

    private Trainee parseTrainee(String[] data) {
        if (data.length >= 5) {
            Long id = Long.parseLong(data[0].trim());
            String firstName = data[1].trim();
            String lastName = data[2].trim();
            boolean active = Boolean.parseBoolean(data[3].trim());
            String address = data[4].trim();
            return new Trainee(id, firstName, lastName, active, null, address);
        }
        return null;
    }

    @Override
    protected Long getIdFromEntity(Trainee trainee) {
        return trainee.getId();
    }
}