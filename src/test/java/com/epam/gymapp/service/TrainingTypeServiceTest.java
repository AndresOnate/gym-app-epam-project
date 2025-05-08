package com.epam.gymapp.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.repository.TrainingTypeRepository;

class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainingTypes_ReturnsList() {
        List<TrainingType> trainingTypes = List.of(new TrainingType(), new TrainingType());
        when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);

        List<TrainingType> result = trainingTypeService.getAllTrainingTypes();

        assertEquals(2, result.size());
        verify(trainingTypeRepository).findAll();
    }

    @Test
    void testGetAllTrainingTypes_ReturnsEmptyList() {
        when(trainingTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<TrainingType> result = trainingTypeService.getAllTrainingTypes();

        assertTrue(result.isEmpty());
        verify(trainingTypeRepository).findAll();
    }
}
