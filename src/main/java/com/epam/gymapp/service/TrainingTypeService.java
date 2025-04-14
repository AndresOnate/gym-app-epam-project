package com.epam.gymapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.repository.TrainingTypeRepository;


@Service
public class TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeService(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeRepository.findAll();
    }
}