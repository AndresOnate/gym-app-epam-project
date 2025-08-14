package com.epam.gymapp.integration.steps;


import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.epam.gymapp.config.JwtUtils;
import com.epam.gymapp.dto.TrainerWorkloadRequest;
import com.epam.gymapp.dto.TrainingDto;
import com.epam.gymapp.utils.WorkloadSpyListener;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TrainingApiSteps {

    @LocalServerPort
    private int port;
    private String transactionId;
    private String baseUrl;
    private RestTemplate restTemplate;
    private TrainerWorkloadRequest lastReceivedWorkload;
    private ResponseEntity<?> lastResponse;
    private WorkloadSpyListener workloadSpyListener;

    @Autowired
    private JwtUtils jwtService;
    
    @Before
    public void setup() {
        baseUrl = "http://localhost:" + port;
        restTemplate = new RestTemplate();
        transactionId = UUID.randomUUID().toString();
        workloadSpyListener = new WorkloadSpyListener();
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
        headers.setBearerAuth(jwtService.generateServiceToken());
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
        assertNull(workloadSpyListener.getLastReceived(), "Expected no TrainerWorkloadRequest to be received.");
    }

    @Then("the API should respond with HTTP {int}")
    public void verifyApiResponse(int statusCode) {
        assertEquals(HttpStatus.valueOf(statusCode), lastResponse.getStatusCode());
    }

    private void waitForMessage() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            if (workloadSpyListener.getLastReceived() != null) break;
            Thread.sleep(500);
        }
        lastReceivedWorkload = workloadSpyListener.getLastReceived();
    }
}