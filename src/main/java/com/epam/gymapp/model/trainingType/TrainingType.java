package com.epam.gymapp.model.trainingType;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The TrainingType enum represents the different types of training available in the gym application.
 * Each training type has a unique ID and a name associated with it.
 */
@Entity
@Table(name = "training_types")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private TrainingTypeEnum name;

    public TrainingType() {}

    public TrainingType(TrainingTypeEnum name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public TrainingTypeEnum getName() { return name; }
}