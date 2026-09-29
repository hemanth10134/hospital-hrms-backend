# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/manpower-planning-service.jar app.jar

# Platform sets $PORT at runtime; default matches application.yml's fallback.
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
