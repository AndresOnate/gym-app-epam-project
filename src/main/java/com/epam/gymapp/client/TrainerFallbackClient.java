package com.epam.gymapp.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.epam.gymapp.dto.TrainerWorkloadRequest;

@Component
public class TrainerFallbackClient implements TrainerWorkloadClient {

    @Override
    public ResponseEntity<String> updateTrainerWorkload(TrainerWorkloadRequest request) {
        return ResponseEntity.ok("Fallback response: Unable to update trainer workload at the moment.");
    }


}
