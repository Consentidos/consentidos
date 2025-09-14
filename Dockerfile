# ================================
# Multi-stage Dockerfile for Spring Boot
# ================================

# Stage 1: Build - Use full JDK image for compilation
FROM eclipse-temurin:17-jdk-alpine AS builder

# Install necessary tools
RUN apk update && apk add --no-cache \
    curl \
    unzip \
    dos2unix

# Set working directory
WORKDIR /app

# Copy Gradle configuration files
COPY gradle gradle/
COPY gradlew .
COPY gradle.properties* .
COPY build.gradle .
COPY settings.gradle .

# Give execution permissions to Gradle wrapper and convert line endings
RUN dos2unix ./gradlew && chmod +x ./gradlew

# Download dependencies (this layer is cached if dependencies don't change)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src/

# Compile and build JAR
RUN ./gradlew build --no-daemon -x test -x checkstyleMain -x checkstyleTest

# ================================
# Stage 2: Runtime - Lightweight image with JRE only
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring \
    && apk add --no-cache curl

# Create directory for the application
WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Change file owner
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Expose port (default Spring Boot port)
EXPOSE 8080

# Environment variables for JVM optimization in containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -XX:+UseG1GC"

# Command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Healthcheck to verify the application is running
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1