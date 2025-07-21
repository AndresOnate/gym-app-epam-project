package com.epam.gymapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.epam.gymapp.dto.TrainerWorkloadRequest;

@FeignClient(value = "trainer-workload-service", fallback = TrainerFallbackClient.class)
public interface TrainerWorkloadClient {

    @PostMapping("/api/trainer-workload/update")
    ResponseEntity<String> updateTrainerWorkload(@RequestBody TrainerWorkloadRequest request);

}