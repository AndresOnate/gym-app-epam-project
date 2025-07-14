package com.epam.gymapp.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.epam.gymapp.repository.*;

import jakarta.persistence.EntityNotFoundException;

import com.epam.gymapp.dto.*;
import com.epam.gymapp.exception.NotFoundException;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.model.trainingType.TrainingTypeEnum;
import com.epam.gymapp.model.user.User;

@SpringBootTest
class TrainerServiceTest {

    @InjectMocks
    private TrainerService trainerService;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Trainer trainer;
    private User user;
    private TrainerDto trainerDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("trainer1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);

        trainer = new Trainer();
        trainer.setUser(user);

        trainerDto = new TrainerDto();
        trainerDto.setUsername("trainer1");
        trainerDto.setFirstName("John");
        trainerDto.setLastName("Doe");
        trainerDto.setSpecialization("STRETCHING");
    }

    @Test
    void testGetAll() {
        when(trainerRepository.findAll()).thenReturn(Collections.singletonList(trainer));

        List<Trainer> trainers = trainerService.getAll();

        assertNotNull(trainers);
        assertEquals(1, trainers.size());
        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    void testGetByIdFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        Trainer found = trainerService.getById(1L);

        assertNotNull(found);
        assertEquals("trainer1", found.getUser().getUsername());
    }

    @Test
    void testGetByIdNotFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        Trainer found = trainerService.getById(1L);

        assertNull(found);
    }



    @Test
    void testUpdateTrainerFound() {
        Trainer updatedTrainer = new Trainer();
        updatedTrainer.setSpecialization(new TrainingType());

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(updatedTrainer);

        Trainer result = trainerService.update(1L, updatedTrainer);

        assertNotNull(result);
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainerNotFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.update(1L, trainer));
    }

    @Test
    void testDelete() {
        doNothing().when(trainerRepository).deleteById(1L);

        trainerService.delete(1L);

        verify(trainerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByUsernameFound() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = trainerService.findByUsername("trainer1");

        assertTrue(result.isPresent());
        assertEquals("trainer1", result.get().getUser().getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.findByUsername("trainer1");

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUnassignedTrainers() {
        when(trainerRepository.findAll()).thenReturn(List.of(trainer));
        when(trainerRepository.findAssignedTrainersByTraineeUsername("trainee1")).thenReturn(Collections.emptyList());

        List<TrainerDto> result = trainerService.getUnassignedTrainers("trainee1");

        assertEquals(1, result.size());
        assertEquals("trainer1", result.get(0).getUsername());
    }

    @Test
    void testGetTrainerProfileByUsernameFound() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer));

        TrainerProfileDto profile = trainerService.getTrainerProfileByUsername("trainer1");

        assertNotNull(profile);
        assertEquals("John", profile.getFirstName());
    }

    @Test
    void testGetTrainerProfileByUsernameNotFound() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainerService.getTrainerProfileByUsername("trainer1"));
    }

    @Test
    void testUpdateTrainerProfileFound() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        TrainerProfileDto requestDto = new TrainerProfileDto();
        requestDto.setFirstName("UpdatedName");
        requestDto.setLastName("UpdatedLast");
        requestDto.setIsActive(false);

        TrainerProfileDto updatedProfile = trainerService.updateTrainerProfile("trainer1", requestDto);

        assertNotNull(updatedProfile);
        assertEquals("UpdatedName", updatedProfile.getFirstName());
        assertEquals("UpdatedLast", updatedProfile.getLastName());
    }

    @Test
    void testUpdateTrainerProfileNotFound() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.empty());

        TrainerProfileDto requestDto = new TrainerProfileDto();
        assertThrows(NotFoundException.class, () -> trainerService.updateTrainerProfile("trainer1", requestDto));
    }

    @Test
    void testUpdateStatus() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        trainerService.updateStatus("trainer1", false);

        assertFalse(trainer.getUser().getIsActive());
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void testUpdateStatusNotFound() {
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.updateStatus("trainer1", true));
    }
}
