CREATE TABLE training_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE trainees (
    trainee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_of_birth DATE,
    address VARCHAR(255),
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_trainee_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE trainers (
    trainer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    specialization_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_trainer_specialization FOREIGN KEY (specialization_id) REFERENCES training_types(id),
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE trainee_trainer (
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    PRIMARY KEY (trainee_id, trainer_id),
    CONSTRAINT fk_tt_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(trainee_id),
    CONSTRAINT fk_tt_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id)
);

CREATE TABLE trainings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trainee_id BIGINT,
    trainer_id BIGINT,
    training_name VARCHAR(255) NOT NULL,
    training_type_id BIGINT,
    training_date DATE NOT NULL,
    training_duration INT NOT NULL,
    CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(trainee_id),
    CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id),
    CONSTRAINT fk_training_type FOREIGN KEY (training_type_id) REFERENCES training_types(id)
);

-- Tipos de entrenamiento
INSERT INTO training_types (id, name) VALUES
(1, 'FITNESS'),
(2, 'YOGA'),
(3, 'ZUMBA'),
(4, 'STRETCHING'),
(5, 'RESISTANCE');

-- Usuarios
INSERT INTO users (user_id, first_name, last_name, username, password, is_active) VALUES
(1, 'John', 'Doe', 'John.Doe', 'pass123', true),
(2, 'Jane', 'Smith', 'Jane.Smith', 'pass123', true),
(3, 'Mike', 'Trainer', 'Mike.Trainer', 'pass123', true),
(4, 'Laura', 'Trainer', 'Laura.Trainer', 'pass123', true),
(5, 'Ana', 'Perez', 'Ana.Perez', 'pass123', true);

-- Trainees
INSERT INTO trainees (trainee_id, date_of_birth, address, user_id) VALUES
(1, '1990-01-01', '123 Street', 1),
(2, '1995-05-15', '456 Avenue', 2),
(3, '1992-03-20', '789 Boulevard', 5);

-- Trainers
INSERT INTO trainers (trainer_id, specialization_id, user_id) VALUES
(1, 1, 3), -- Mike Trainer, especialidad FITNESS
(2, 2, 4); -- Laura Trainer, especialidad YOGA

-- Relación ManyToMany trainee-trainer
INSERT INTO trainee_trainer (trainee_id, trainer_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 2);

