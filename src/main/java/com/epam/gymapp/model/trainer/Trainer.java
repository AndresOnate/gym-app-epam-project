package com.epam.gymapp.model.trainer;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import com.epam.gymapp.dto.TrainerDto;
import com.epam.gymapp.model.trainee.Trainee;
import com.epam.gymapp.model.trainingType.TrainingType;
import com.epam.gymapp.model.trainingType.TrainingTypeEnum;
import com.epam.gymapp.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Represents a trainer in the gym system.
 * Extends the {@code User} class, inheriting basic user attributes and methods.
 */
@Entity
@Table(name = "trainers")
public class Trainer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trainer_id")
    private Long id;             // The unique identifier of the trainer

    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;   // The area of specialization of the trainer

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

     @ManyToMany(mappedBy = "trainers")  // Relación inversa
    private Set<Trainee> trainees = new HashSet<>();  // Set de Trainees asociados con el Trainer


    /**
     * Default constructor for the {@code Trainer} class.
     */
    public Trainer() {}

    /**
     * Constructs a trainer with all attributes initialized.
     *
     * @param id Unique identifier of the trainer.
     * @param firstName First name of the trainer.
     * @param lastName Last name of the trainer.
     * @param username Trainer's username.
     * @param password Trainer's password.
     * @param isActive Activation status of the trainer in the system.
     * @param trainingType The type of training the trainer provides.
     * @param specialization The trainer's area of specialization.
     */
    public Trainer(TrainingType trainingType) {
        this.specialization = trainingType;
    }

    public Trainer(Long id, TrainingType specialization, User user, Set<Trainee> trainees) {
        this.id = id;
        this.specialization = specialization;
        this.user = user;
        this.trainees = trainees;
    }

    public Trainer(TrainerDto trainerDto) {
        this.user = new User(trainerDto.getFirstName(), trainerDto.getLastName(), true);
        this.specialization = new TrainingType(TrainingTypeEnum.valueOf(trainerDto.getSpecialization().toUpperCase()));
    }

    /**
     * Gets the trainer's specialization.
     *
     * @return The trainer's specialization.
     */
    public TrainingType getSpecialization() {
        return specialization;
    }

    /**
     * Sets the trainer's specialization.
     *
     * @param specialization The new specialization.
     */
    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }


    /**
     * Retrieves the user associated with this trainer.
     * 
     * This method returns the reference to the {@link User} object representing
     * the user linked to the trainer. The relationship is one-to-one.
     *
     * @return The {@link User} object associated with this trainer.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this trainer.
     * 
     * This method assigns a {@link User} object to this trainer. It establishes
     * the one-to-one relationship between the trainer and the user.
     *
     * @param user The {@link User} object representing the user to be associated with the trainer.
     * @throws IllegalArgumentException If the provided user is {@code null}.
     */
    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        this.user = user;
    }

    

    public Set<Trainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Set<Trainee> trainees) {
        this.trainees = trainees;
    }

    /**
     * Returns a string representation of the trainer.
     *
     * @return A string containing the trainer's details.
     */
    @Override
    public String toString() {
    return "Trainer{" +
           "id=" + id +  
           ", firstName='" + user.getFirstName() + '\'' +  
           ", lastName='" + user.getLastName() + '\'' + 
           ", isActive=" + user.getIsActive() + 
           ", specialization=" + specialization +
           '}';
    }

}
