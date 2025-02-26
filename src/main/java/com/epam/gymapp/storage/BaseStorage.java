package com.epam.gymapp.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseStorage<T> {

    private static final Logger logger = Logger.getLogger(BaseStorage.class.getName());

    protected final Map<Long, T> storage;

    protected BaseStorage(Map<Long, T> storage) {
        this.storage = storage;
    }

    /**
     * Carga datos desde un archivo y los procesa.
     *
     * @param filePath Ruta del archivo.
     * @param parser   Función para convertir una línea del archivo en un objeto.
     */
    protected void loadDataFromFile(String filePath, Function<String[], T> parser) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true; // Para ignorar el encabezado
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length > 0) {
                    T entity = parser.apply(data);
                    if (entity != null) {
                        storage.put(getIdFromEntity(entity), entity);
                        logger.log(Level.INFO, "Loaded Entity: {0}", entity);
                    }
                }
            }
            logger.info("Storage initialized with data from file: " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading data from file: " + filePath, e);
        }
    }

    /**
     * Obtiene el ID de la entidad.
     *
     * @param entity La entidad.
     * @return El ID de la entidad.
     */
    protected abstract Long getIdFromEntity(T entity);

    public Map<Long, T> getStorage() {
        return storage;
    }
}