package com.epam.gymapp.model.training;

/**
 * The TrainingType enum represents the different types of training available in the gym application.
 * Each training type has a unique ID and a name associated with it.
 */
public enum TrainingType {

    FITNESS(1L, "fitness"),      // Represents fitness training
    YOGA(2L, "yoga"),            // Represents yoga training
    ZUMBA(3L, "zumba"),          // Represents zumba training
    STRETCHING(4L, "stretching"),// Represents stretching training
    RESISTANCE(5L, "resistance");// Represents resistance training

    private final Long id;       // The unique identifier for the training type
    private final String name;   // The name of the training type

    /**
     * Private constructor for the TrainingType enum.
     *
     * @param id The unique identifier for the training type.
     * @param name The name of the training type.
     */
    private TrainingType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public static boolean isValid(String name) {
        for (TrainingType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static TrainingType from(Long id) {
        for(TrainingType trainingType: values()) {
            if(trainingType.getId().equals(id))
                return trainingType;
        }

        throw new IllegalArgumentException("Cannot get training type for ID " + id);
    }


    @Override
    public String toString() {
        return "TrainingType{id=" + id + ", name='" + name + "'}";
    }
}