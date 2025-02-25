package com.epam.gymapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.model.training.TrainingType;
import com.epam.gymapp.repository.TrainingDao;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService trainingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAll() {
        Training training1 = new Training(1L, 101L, 201L, "Cardio", TrainingType.YOGA, new Date(System.currentTimeMillis()), 60);
        Training training2 = new Training(2L, 102L, 202L, "Strength", TrainingType.FITNESS, new Date(System.currentTimeMillis()), 45);
        List<Training> trainings = List.of(training1, training2);

        when(trainingDao.getAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cardio", result.get(0).getTrainingName());
        assertEquals("Strength", result.get(1).getTrainingName());
        verify(trainingDao, times(1)).getAll();
    }

    @Test
    public void testGetById() {
        Training training = new Training(1L, 101L, 201L, "Cardio", TrainingType.YOGA, new Date(System.currentTimeMillis()), 60);

        when(trainingDao.getById(1L)).thenReturn(Optional.of(training));
        Training result = trainingService.getById(1L);
        assertNotNull(result);
        assertEquals("Cardio", result.getTrainingName());
        verify(trainingDao, times(1)).getById(1L);
    }

    @Test
    public void testSave() {

        Training training = new Training(2L, 102L, 202L, "Strength", TrainingType.ZUMBA, new Date(System.currentTimeMillis()), 45);

        when(trainingDao.save(training)).thenReturn(training);

        Training savedTraining = trainingService.save(training);

        assertNotNull(savedTraining);
        assertEquals("Strength", savedTraining.getTrainingName());
        verify(trainingDao, times(1)).save(training);
    }

    @Test
    public void testUpdate() {

        Training originalTraining = new Training(1L, 101L, 201L, "Cardio", TrainingType.YOGA, new Date(System.currentTimeMillis()), 60);
        Training updatedTraining = new Training(1L, 101L, 201L, "Strength", TrainingType.ZUMBA, new Date(System.currentTimeMillis()), 75);

        when(trainingDao.update(1L, updatedTraining)).thenReturn(updatedTraining);

        Training result = trainingService.update(1L, updatedTraining);
    
        // Verificar resultados
        assertNotNull(result);
        assertEquals("Strength", result.getTrainingName());
        assertEquals(75, result.getTrainingDuration());
        verify(trainingDao, times(1)).update(1L, updatedTraining);
    }

    @Test
    public void testDelete() {
        trainingService.delete(1L);
        verify(trainingDao, times(1)).delete(1L);
    }
}
