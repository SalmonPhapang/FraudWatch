
# Runtime stage only (we already built the jar locally)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar file from local target directory
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
