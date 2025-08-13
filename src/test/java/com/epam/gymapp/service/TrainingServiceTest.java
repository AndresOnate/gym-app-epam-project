package com.epam.gymapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.model.trainingType.TrainingTypeEnum;
import com.epam.gymapp.model.user.User;
import com.epam.gymapp.repository.*;

import jakarta.persistence.EntityNotFoundException;

class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingPublisher trainingPublisher;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        when(trainingRepository.findAll()).thenReturn(List.of(new Training()));

        List<Training> result = trainingService.getAll();

        assertEquals(1, result.size());
        verify(trainingRepository).findAll();
    }

    @Test
    void testGetById_Found() {
        Training training = new Training();
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Training result = trainingService.getById(1L);

        assertEquals(training, result);
    }

    @Test
    void testGetById_NotFound() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        Training result = trainingService.getById(1L);

        assertNull(result);
    }



    @Test
    void testSave_TrainingDto() {
        TrainingDto dto = new TrainingDto();
        dto.setTrainingName("Session");
        dto.setTrainingDuration(60);
        dto.setTrainingTypeName("Yoga");
        dto.setTrainerUsername("trainer1");
        dto.setTraineeUsername("trainee1");

        TrainingType type = new TrainingType(TrainingTypeEnum.YOGA);
        Trainer trainer = new Trainer();
        trainer.setUser(new User());
        Trainee trainee = new Trainee();
        trainee.setUser(new User());

        when(trainingTypeRepository.findByName(TrainingTypeEnum.YOGA)).thenReturn(Optional.of(type));
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(traineeRepository.findByUserUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainingRepository.save(any(Training.class))).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(trainingPublisher).sendTraining(any());

        Training result = trainingService.save(dto);

        assertNotNull(result);
        assertEquals("Session", result.getTrainingName());
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void testUpdate_Found() {
        Training existing = new Training();
        existing.setTrainingName("Old");
        existing.setTrainingDuration(30);

        Training updated = new Training();
        updated.setTrainingName("New");
        updated.setTrainingDuration(45);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(trainingRepository.save(existing)).thenReturn(existing);

        Training result = trainingService.update(1L, updated);

        assertEquals("New", result.getTrainingName());
        assertEquals(45, result.getTrainingDuration());
    }

    @Test
    void testUpdate_NotFound() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.update(1L, new Training()));
    }

    @Test
    void testDelete() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(new Training()));
        doNothing().when(trainingRepository).deleteById(1L);

        trainingService.delete(1L);
        verify(trainingRepository).findById(1L);
        verify(trainingRepository).deleteById(1L);
    }

    @Test
    void testGetTraineeTrainings() {
        Training training = new Training();
        training.setTrainingName("Yoga");
        TrainingType trainingType = new TrainingType(TrainingTypeEnum.YOGA);
        training.setTrainingType(trainingType);
        training.setTrainingDuration(45);

        when(trainingRepository.findByTraineeUserUsernameAndTrainingDateBetweenAndTrainerUserUsernameContainingAndTrainingTypeNameContaining(
                anyString(), any(Date.class), any(Date.class), anyString(), anyString()))
                .thenReturn(List.of(training));

        List<TrainingDto> result = trainingService.getTraineeTrainings("trainee1", new Date(1), new Date(2L), "", "");

        assertEquals(1, result.size());
        assertEquals("Yoga", result.get(0).getTrainingName());
    }

    @Test
    void testGetTrainerTrainings() {
        Training training = new Training();
        training.setTrainingName("Strength Training");
        TrainingType trainingType = new TrainingType(TrainingTypeEnum.STRETCHING);
        training.setTrainingType(trainingType);
        training.setTrainingDuration(60);

        when(trainingRepository.findByTrainerUserUsernameAndTrainingDateBetweenAndTraineeUserUsernameContaining(
                anyString(), any(Date.class), any(Date.class), anyString()))
                .thenReturn(List.of(training));

        List<TrainingDto> result = trainingService.getTrainerTrainings("trainer1", new Date(1), new Date(2L), "");

        assertEquals(1, result.size());
        assertEquals("Strength Training", result.get(0).getTrainingName());
    }
}
