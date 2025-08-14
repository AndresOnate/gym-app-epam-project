Feature: Crear entrenamiento y actualizar carga del trainer

  Scenario: Crear una sesión de entrenamiento y actualizar carga
    When a training session is created with trainer "Mike.Trainer" and trainee "John.Doe"
    Then the trainer workload should be updated in the receiving service

  Scenario: Intentar crear un entrenamiento con un trainer inexistente
    When a training session is created with trainer "No.Existe" and trainee "John.Doe"
    Then the API should respond with HTTP 400
    And no workload update should be received

  Scenario: Intentar crear un entrenamiento con tipo incorrecto
    When a training session is created with trainer "Mike.Trainer" and trainee "John.Doe" on "ERROR_TYPE"
    Then the API should respond with HTTP 400
    And no workload update should be received

  Scenario: Intentar crear un entrenamiento con tipo inexistente
    When a training session is created with trainer "Mike.Trainer" and trainee "John.Doe" with type "ZUMBA_EXTREMO"
    Then the API should respond with HTTP 400
    And no workload update should be received
