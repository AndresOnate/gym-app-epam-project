package com.epam.gymapp.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.model.training.TrainingType;
import jakarta.annotation.PostConstruct;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingStorage extends BaseStorage<Training> {

    @Value("${storage.training.file.path}")
    private String filePath;

    public TrainingStorage() {
        super(new HashMap<>());
    }

    @PostConstruct
    private void init() {
        loadDataFromFile(filePath, this::parseTraining);
    }

    public Map<Long, Training> getTrainings() {
        return storage;
    }

    private Training parseTraining(String[] data) {
        if (data.length >= 7) {
            Long id = Long.parseLong(data[0].trim());
            Long traineeId = Long.parseLong(data[1].trim());
            Long trainerId = Long.parseLong(data[2].trim());
            String trainingName = data[3].trim();
            TrainingType trainingType = TrainingType.valueOf(data[4].trim().toUpperCase());
            Date trainingDate = Date.valueOf(data[5].trim());
            int trainingDuration = Integer.parseInt(data[6].trim());
            return new Training(id, traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
        }
        return null;
    }

    @Override
    protected Long getIdFromEntity(Training training) {
        return training.getId();
    }
}