package com.epam.gymapp.storage;

import com.epam.gymapp.model.trainee.Trainee;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Storage component for managing trainees.
 * Loads initial data from a file during application startup.
 */
@Component
public class TraineeStorage {

    private final Map<Long, Trainee> trainees = new HashMap<>();

    @Value("${storage.trainee.file.path}")
    private String filePath;

    /**
     * Initializes the trainee storage by loading data from a file at startup.
     */
    @PostConstruct
    private void init() {
        loadDataFromFile();
    }

    public Map<Long, Trainee> getTrainees() {
        return trainees;
    }

    private void loadDataFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;  // Para ignorar el encabezado
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;  // Saltar la primera línea
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 5) {
                    Long id = Long.parseLong(data[0].trim());
                    String firstName = data[1].trim();
                    String lastName = data[2].trim();
                    boolean active = Boolean.parseBoolean(data[3].trim());
                    String address = data[4].trim();

                    Trainee trainee = new Trainee(id, firstName, lastName, active, null, address);
                    trainees.put(id, trainee);
                    System.out.println("Loaded Trainee: " + trainee);
                }
            }
            System.out.println("Trainee storage initialized with data from file.");
        } catch (IOException e) {
            System.err.println("Error loading trainee data: " + e.getMessage());
        }
    }
}
