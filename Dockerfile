FROM alpine:3.18.4

WORKDIR /app

RUN apk add --no-cache java-cacerts openjdk17-jre

# Copy the JAR file from the first stage into the second stage
COPY /topic-worker.jar .

# Define the command to run your Spring Boot application
CMD ["java", "-jar", "topic-worker.jar"]