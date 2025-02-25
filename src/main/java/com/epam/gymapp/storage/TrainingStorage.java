package com.epam.gymapp.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.epam.gymapp.model.training.Training;
import com.epam.gymapp.model.training.TrainingType;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingStorage {

    private final Map<Long, Training> trainings = new HashMap<>();

    @Value("${storage.training.file.path}")
    private String filePath; // Ruta del archivo CSV

    /**
     * Inicializa el almacenamiento de entrenamientos cargando datos desde un archivo CSV.
     */
    @PostConstruct
    public void init() {
        loadDataFromFile();
    }

    /**
     * Obtiene el almacenamiento de entrenamientos.
     *
     * @return El Map de entrenamientos.
     */
    public Map<Long, Training> getStorage() {
        return trainings;
    }

    /**
     * Carga los datos del archivo CSV en el almacenamiento de entrenamientos.
     */
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
                if (data.length >= 7) {
                    Long id = Long.parseLong(data[0].trim());
                    Long traineeId = Long.parseLong(data[1].trim());
                    Long trainerId = Long.parseLong(data[2].trim());
                    String trainingName = data[3].trim();
                    TrainingType trainingType = TrainingType.valueOf(data[4].trim().toUpperCase());
                    Date trainingDate = Date.valueOf(data[5].trim()); 
                    int trainingDuration = Integer.parseInt(data[6].trim());

                    Training training = new Training(id, traineeId, trainerId, trainingName, trainingType, trainingDate, trainingDuration);
                    trainings.put(id, training);
                    System.out.println("Loaded training: " + training);
                }
            }
            System.out.println("Training storage initialized with data from file.");
        } catch (IOException e) {
            System.err.println("Error loading training data: " + e.getMessage());
        }
    }
}