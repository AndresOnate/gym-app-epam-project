# Stage 1: Build
FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiamos el jar construido
COPY --from=builder /app/target/gymapp-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto configurado en server.port
EXPOSE 8080

# Activamos un perfil "docker" (que puedes definir en application-docker.properties)
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=local"]