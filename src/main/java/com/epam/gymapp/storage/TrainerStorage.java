package com.epam.gymapp.storage;


import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.training.TrainingType;
import jakarta.annotation.PostConstruct;

@Component
public class TrainerStorage extends BaseStorage<Trainer> {

    @Value("${storage.trainer.file.path}")
    private String filePath;

    public TrainerStorage() {
        super(new HashMap<>());
    }

    @PostConstruct
    private void init() {
        loadDataFromFile(filePath, this::parseTrainer);
    }

    public Map<Long, Trainer> getTrainers() {
        return storage;
    }

    private Trainer parseTrainer(String[] data) {
        if (data.length >= 5) {
            Long id = Long.parseLong(data[0].trim());
            String firstName = data[1].trim();
            String lastName = data[2].trim();
            boolean isActive = Boolean.parseBoolean(data[3].trim());
            TrainingType specialization = TrainingType.valueOf(data[4].trim().toUpperCase());
            return new Trainer(id, firstName, lastName, isActive, specialization);
        }
        return null;
    }

    @Override
    protected Long getIdFromEntity(Trainer trainer) {
        return trainer.getId();
    }
}