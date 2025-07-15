# Use a lightweight OpenJDK 17 image as the base
FROM openjdk:22-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files to enable building inside the container
COPY mvnw .
COPY .mvn .mvn

# Copy the pom.xml file to download dependencies first (leveraging Docker layer caching)
COPY pom.xml .

# Download dependencies - this step will be cached unless pom.xml changes
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the application source code
COPY src src

# Build the Spring Boot application into a JAR file
# -DskipTests skips running tests during the Docker build, which is common for faster builds
RUN ./mvnw package -DskipTests

# Expose the port your Spring Boot application runs on (default is 8080)
EXPOSE 8080

# Command to run the application
# Assumes your JAR file is named 'backend-0.0.1-SNAPSHOT.jar'
# You might need to adjust the JAR name based on your project's build output.
# You can find the exact name in your 'target' directory after a local build.
ENTRYPOINT ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
