Feature:  Crear entrenamientos

  Scenario: Crear una sesión de entrenamiento y actualizar carga
    When a training session is created with trainer "Mike.Trainer" and trainee "John.Doe"
    Then the trainer workload should be updated in the receiving service

  Scenario: Crear una sesión de entrenamiento de tipo "YOGA"
    When a training session is created with trainer "Mike.Trainer" and trainee "John.Doe" with type "YOGA"
    Then the trainer workload should be updated in the receiving service

  Scenario: Crear una sesión de entrenamiento de tipo "CARDIO"
    When a training session is created with trainer "Mike.Trainer" and trainee "Jane.Smith" with type "STRETCHING"
    Then the trainer workload should be updated in the receiving service
