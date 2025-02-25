package com.epam.gymapp.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.epam.gymapp.model.trainer.Trainer;
import com.epam.gymapp.model.training.TrainingType;

import jakarta.annotation.PostConstruct;

@Component
public class TrainerStorage {
      
    private final Map<Long, Trainer> storage = new HashMap<>();

    @Value("${storage.trainer.file.path}")
    private String filePath;

    @PostConstruct
    public void initializeStorage() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;  // Para ignorar el encabezado
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;  // Saltar la primera línea
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 4) { 
                    Long id = Long.parseLong(data[0].trim());
                    String firstName = data[1].trim();
                    String lastName = data[2].trim();
                    Boolean isActive = Boolean.parseBoolean(data[3].trim());
                    String specializationStr = data[4].trim(); 
                    TrainingType specialization = TrainingType.valueOf(specializationStr.toUpperCase());

                    Trainer trainer = new Trainer(id, firstName, lastName, isActive, specialization);
                    System.out.println("Loaded Trainer: " + trainer);
                    storage.put(id, trainer);
                }
            }
            System.out.println("Trainer storage initialized with data from file.");
        } catch (IOException e) {
            System.err.println("Error loading trainer data: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error mapping specialization: " + e.getMessage());
        }
    }

    public Map<Long, Trainer> getStorage() {
        return storage;
    }
}
