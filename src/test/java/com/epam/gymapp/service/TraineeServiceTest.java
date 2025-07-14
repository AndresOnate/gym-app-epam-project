package com.epam.gymapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.gymapp.dto.RegistrationDto;
import com.epam.gymapp.dto.TraineeDto;
import com.epam.gymapp.dto.TraineeProfileDto;
import com.epam.gymapp.dto.TrainerDto;
import com.epam.gymapp.exception.NotFoundException;
import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.user.User;
import com.epam.gymapp.repository.TraineeRepository;
import com.epam.gymapp.repository.TrainerRepository;
import com.epam.gymapp.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock 
    private TrainerRepository trainerRepository; 

    @InjectMocks
    private TraineeService traineeService;

    @Test
    void testGetAll() {
        List<Trainee> trainees = Arrays.asList(new Trainee(), new Trainee());
        when(traineeRepository.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAll();

        assertEquals(2, result.size());
        verify(traineeRepository).findAll();
    }

    @Test
    void testGetByIdFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getById(1L);

        assertNotNull(result);
        assertEquals(trainee, result);
    }

    @Test
    void testGetByIdNotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        Trainee result = traineeService.getById(1L);

        assertNull(result);
    }



    @Test
    void testUpdateFound() {
        Trainee existing = new Trainee();
        existing.setAddress("Old Address");

        Trainee updated = new Trainee();
        updated.setAddress("New Address");

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(traineeRepository.save(existing)).thenReturn(existing);

        Trainee result = traineeService.update(1L, updated);

        assertEquals("New Address", result.getAddress());
    }

    @Test
    void testUpdateNotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.update(1L, new Trainee()));
    }

    @Test
    void testDeleteTraineeFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee(1L);

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void testDeleteTraineeNotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.deleteTrainee(1L));
    }

    @Test
    void testFindByUsernameFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = traineeService.findByUsername("user");

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.findByUsername("user");

        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateTraineeTrainersBySet() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        Set<Trainer> trainers = new HashSet<>(Arrays.asList(new Trainer(), new Trainer()));

        traineeService.updateTraineeTrainers("user", trainers);

        verify(traineeRepository).save(trainee);
        assertEquals(2, trainee.getTrainers().size());
    }

    @Test
    void testUpdateTraineeTrainersByListSuccess() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        Trainer trainer1 = new Trainer();
        trainer1.setUser(new User("trainer1", "First", true));
        Trainer trainer2 = new Trainer();
        trainer2.setUser(new User("trainer2", "First2", true));

        when(trainerRepository.findByUserUsernameIn(Arrays.asList("trainer1", "trainer2")))
            .thenReturn(Arrays.asList(trainer1, trainer2));

        List<TrainerDto> result = traineeService.updateTraineeTrainers("user", Arrays.asList("trainer1", "trainer2"));

        assertEquals(2, result.size());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testUpdateTraineeTrainersByListNotFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        when(trainerRepository.findByUserUsernameIn(Arrays.asList("trainer1"))).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> traineeService.updateTraineeTrainers("user", Arrays.asList("trainer1")));
    }

    @Test
    void testGetProfileByUsernameSuccess() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);
        trainee.setUser(user);
        trainee.setTrainers(new HashSet<>());

        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        TraineeProfileDto profile = traineeService.getProfileByUsername("user");

        assertEquals("John", profile.getFirstName());
        assertTrue(profile.isActive());
    }

    @Test
    void testGetProfileByUsernameNotFound() {
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.getProfileByUsername("user"));
    }

    @Test
    void testUpdateProfileSuccess() {
        Trainee trainee = new Trainee();
        User user = new User();
        trainee.setUser(user);
        trainee.setTrainers(new HashSet<>());
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        TraineeProfileDto request = new TraineeProfileDto();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setAddress("New Address");
        request.setActive(true);

        TraineeProfileDto result = traineeService.updateProfile("user", request);

        assertEquals("Updated", result.getFirstName());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testUpdateProfileNotFound() {
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.empty());

        TraineeProfileDto request = new TraineeProfileDto();

        assertThrows(NotFoundException.class, () -> traineeService.updateProfile("user", request));
    }

    @Test
    void testDeleteTraineeByUsernameSuccess() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeByUsername("user");

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void testDeleteTraineeByUsernameNotFound() {
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.deleteTraineeByUsername("user"));
    }

    @Test
    void testUpdateStatusSuccess() {
        Trainee trainee = new Trainee();
        User user = new User();
        trainee.setUser(user);
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.of(trainee));

        traineeService.updateStatus("user", true);

        assertTrue(user.getIsActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void testUpdateStatusNotFound() {
        when(traineeRepository.findByUserUsername("user")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.updateStatus("user", true));
    }
}
