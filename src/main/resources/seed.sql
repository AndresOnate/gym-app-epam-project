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