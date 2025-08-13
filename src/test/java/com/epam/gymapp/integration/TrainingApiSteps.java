package com.epam.gymapp.integration;


import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import com.epam.gymapp.dto.TraineeDto;
import com.epam.gymapp.dto.TrainerDto;
import com.epam.gymapp.dto.TrainerWorkloadRequest;
import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.model.trainingType.TrainingTypeEnum;
import com.epam.gymapp.repository.*;
import com.epam.gymapp.service.TraineeService;
import com.epam.gymapp.service.TrainerService;
import com.epam.gymapp.utils.NoSecurityConfig;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@CucumberContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(NoSecurityConfig.class)
public class TrainingApiSteps {

    @LocalServerPort
    private int port;
    private String baseUrl;
    private RestTemplate restTemplate;
    private TrainerWorkloadRequest lastReceivedWorkload;
    private ResponseEntity<?> lastResponse;

    @Autowired
    private EntityManager em;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Before
    public void setup() {
        baseUrl = "http://localhost:" + port;
        restTemplate = new RestTemplate();
        WorkloadSpyListener.clear();
    }

    @Given("a trainer {string} and a trainee {string} exist in the system")
    public void setupUsers(String trainer, String trainee) {
        assertNotNull(trainerService.findByUsername(trainer),
            "Trainer with username " + trainer + " should exist in DB");
        assertNotNull(traineeService.findByUsername(trainee),
            "Trainee with username " + trainee + " should exist in DB");
        assertNotNull(trainingTypeRepository.findByName(TrainingTypeEnum.YOGA),
            "Training type YOGA should exist in DB");
    }

    @When("a training session is created with trainer {string} and trainee {string}")
    public void createTrainingSession(String trainer, String trainee) {
        createTraining(trainer, trainee, "YOGA", LocalDate.of(2023, 10, 1));
    }

    @When("a training session is created with trainer {string} and trainee {string} on {string}")
    public void createTrainingSessionWithDate(String trainer, String trainee, String type) {
        createTraining(trainer, trainee, type, LocalDate.of(2023, 10, 1));
    }

    @When("a training session is created with trainer {string} and trainee {string} with type {string}")
    public void createTrainingSessionWithType(String trainer, String trainee, String type) {
        createTraining(trainer, trainee, type, LocalDate.of(2023, 10, 1));
    }

    @When("two training sessions are created for trainer {string} and trainee {string}")
    public void createTwoTrainings(String trainer, String trainee) {
        createTraining(trainer, trainee, "YOGA", LocalDate.of(2023, 10, 1));
        createTraining(trainer, trainee, "YOGA", LocalDate.of(2023, 10, 2));
    }

    private void createTraining(String trainer, String trainee, String type, LocalDate date) {
        TrainingDto request = new TrainingDto();
        request.setTrainerUsername(trainer);
        request.setTraineeUsername(trainee);
        request.setTrainingName("Sesión de " + type);
        request.setTrainingDate(java.sql.Date.valueOf(date));
        request.setTrainingDuration(60);
        request.setTrainingTypeName(type);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainingDto> entity = new HttpEntity<>(request, headers);

        try {
            lastResponse = restTemplate.postForEntity(baseUrl + "/api/v1/trainings", entity, Void.class);
        } catch (HttpClientErrorException e) {
            lastResponse = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @Then("the trainer workload should be updated in the receiving service")
    public void verifyWorkloadWasUpdated() throws InterruptedException {
        waitForMessage();
        assertNotNull(lastReceivedWorkload, "Expected a TrainerWorkloadRequest to be received.");
    }

    @Then("no workload update should be received")
    public void verifyNoWorkloadUpdate() throws InterruptedException {
        Thread.sleep(2000);
        assertNull(WorkloadSpyListener.getLastReceived(), "Expected no TrainerWorkloadRequest to be received.");
    }

    @Then("the API should respond with HTTP {int}")
    public void verifyApiResponse(int statusCode) {
        assertEquals(HttpStatus.valueOf(statusCode), lastResponse.getStatusCode());
    }

    private void waitForMessage() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            if (WorkloadSpyListener.getLastReceived() != null) break;
            Thread.sleep(500);
        }
        lastReceivedWorkload = WorkloadSpyListener.getLastReceived();
    }
}