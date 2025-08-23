# Build the jar first using Maven
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Build jar
RUN mvn package -DskipTests

# Create image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy jar from the build stage
COPY --from=build /app/target/pmt-backend-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-postgres.sh wait-for-postgres.sh
RUN chmod +x wait-for-postgres.sh

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
