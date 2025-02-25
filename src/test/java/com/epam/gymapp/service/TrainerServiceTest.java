package com.epam.gymapp.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.training.TrainingType;
import com.epam.gymapp.repository.TrainerDao;

public class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerService trainerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); 
    }

    @Test
    public void testGetAll() {
        Trainer trainer1 = new Trainer(1L, "John", "Doe",  true, TrainingType.FITNESS);
        Trainer trainer2 = new Trainer(2L, "Jane", "Doe", true, TrainingType.YOGA);
        List<Trainer> trainers = List.of(trainer1, trainer2);

        when(trainerDao.getAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        verify(trainerDao, times(1)).getAll();
    }

    @Test
    public void testGetById() {
        Trainer trainer = new Trainer(1L, "John", "Doe", true, TrainingType.FITNESS);

        when(trainerDao.getById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(trainerDao, times(1)).getById(1L);
    }

    @Test
    public void testSave() {

        Trainer trainer = new Trainer(2L, "Jane", "Doe", true, TrainingType.YOGA);

        when(trainerDao.save(trainer)).thenReturn(trainer);

        Trainer savedTrainer = trainerService.save(trainer);
        assertNotNull(savedTrainer);
        assertEquals("Jane", savedTrainer.getFirstName());
        verify(trainerDao, times(1)).save(trainer);
    }

    @Test
    public void testUpdate() {

        Trainer trainer = new Trainer(1L, "John", "Doe" , true, TrainingType.FITNESS);
        Trainer updatedTrainer = new Trainer(1L, "John", "Smith", true, TrainingType.YOGA);

        when(trainerDao.update(1L, updatedTrainer)).thenReturn(updatedTrainer);
        Trainer result = trainerService.update(1L, updatedTrainer);


        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(trainerDao, times(1)).update(1L, updatedTrainer);
    }

    @Test
    public void testDelete() {
        Trainer trainer = new Trainer(1L, "John", "Doe", true, TrainingType.FITNESS);
        trainerService.delete(1L);
        verify(trainerDao, times(1)).delete(1L);
    }
}