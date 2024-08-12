# Step 1: Use an official Gradle image as a build environment
FROM gradle:8.1.1-jdk17 AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle /app/gradle
COPY build.gradle settings.gradle /app/

# Copy the source code
COPY src /app/src

# Grant execution permissions to the Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build --exclude-task test

# Step 2: Use an official OpenJDK image as a runtime environment
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create logs directory and set permissions
RUN mkdir -p /app/logs && \
    chmod 755 /app/logs

# Set environment variable for logging
ENV LOG_PATH=/app/logs

# Expose the application port
EXPOSE 8080

# Define the entry point for the container
ENTRYPOINT ["java", "-Dlogging.file.path=/app/logs/app.log", "-jar", "/app/app.jar"]

# Logs directory volume
VOLUME ["/app/logs"]
