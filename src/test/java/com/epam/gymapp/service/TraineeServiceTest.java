package com.epam.gymapp.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.repository.TraineeDao;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); 
        trainee = new Trainee(1L, "John", "Doe", true, new Date(System.currentTimeMillis()), "123 Main St");
    }

    @Test
    public void testGetAll() {
        List<Trainee> trainees = Collections.singletonList(trainee);
        when(traineeDao.getAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(traineeDao, times(1)).getAll();  
    }

    @Test
    public void testGetById_ValidId() {

        when(traineeDao.getById(1L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getById(1L);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDao, times(1)).getById(1L);
    }

    @Test
    public void testGetById_InvalidId() {
        when(traineeDao.getById(999L)).thenReturn(Optional.empty());
        Trainee result = traineeService.getById(999L);
        assertNull(result);
        verify(traineeDao, times(1)).getById(999L);
    }

    @Test
    public void testSave() {
        when(traineeDao.save(trainee)).thenReturn(trainee);
        Trainee result = traineeService.save(trainee);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDao, times(1)).save(trainee);
    }

    @Test
    public void testUpdate() {
        when(traineeDao.update(1L, trainee)).thenReturn(trainee);
        Trainee result = traineeService.update(1L, trainee);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDao, times(1)).update(1L, trainee);
    }

    @Test
    public void testDelete() {
        traineeService.delete(1L);
        verify(traineeDao, times(1)).delete(1L);
    }
}