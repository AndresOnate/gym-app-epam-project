package com.epam.gymapp.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import com.epam.gymapp.dto.TrainerWorkloadRequest;

@Service
public class TrainingPublisher{

    private final JmsTemplate jmsTemplate;

    public TrainingPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendTraining(TrainerWorkloadRequest dto) {
        jmsTemplate.convertAndSend("training.queue", dto);
    }
}