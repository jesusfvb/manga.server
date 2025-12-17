# Stage 1: Build the JAR (using Maven/JDK)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first (Optimization)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime (using Playwright)
FROM mcr.microsoft.com/playwright/java:v1.45.0-jammy

WORKDIR /app

# Install Java 21 (Playwright images include browser deps, but you need JRE/JDK to run the JAR)
RUN apt-get update && apt-get install -y openjdk-21-jre-headless curl && rm -rf /var/lib/apt/lists/*

# Create non-root user and set permissions
RUN useradd -m spring && chown -R spring:spring /app
USER spring

# Copy the JAR from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]