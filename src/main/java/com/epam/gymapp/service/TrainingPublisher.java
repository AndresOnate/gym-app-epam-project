package com.epam.gymapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.epam.gymapp.dto.TrainerWorkloadRequest;

@Service
public class TrainingPublisher{

    private final JmsTemplate jmsTemplate;
     private static final Logger operationLogger = LoggerFactory.getLogger("listenerLogger");


    public TrainingPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendTraining(TrainerWorkloadRequest dto) {
        try {
            jmsTemplate.convertAndSend("training.queue", dto);
            operationLogger.info("Sent workload update for trainer: {}", dto.getUsername());
        } catch (Exception e) {
            operationLogger.error("Failed to send workload update: {}", e.getMessage(), e);
        }
    }
}