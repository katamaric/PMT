# Use an official OpenJDK image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar (build it with Maven before running Docker)
COPY target/pmt-backend-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-postgres.sh wait-for-postgres.sh

RUN chmod +x wait-for-postgres.sh

# Expose the port the app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
