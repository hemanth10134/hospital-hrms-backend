# Hospital HRMS - Backend (Microservices)

Java 17+, Spring Boot 3, Maven, PostgreSQL. Each module under this repo is an
independently runnable Spring Boot microservice.

## Modules

- `manpower-planning-service` — Manpower Planning (staffing ratios, designation-wise
  requirement calculation, additional position requests). See its
  [API contract](manpower-planning-service/API-CONTRACT.md).

## Running locally

1. Start PostgreSQL:
   ```bash
   docker compose up -d
   ```
2. Run a service (Flyway migrates the schema and seeds reference data automatically):
   ```bash
   cd manpower-planning-service
   mvn spring-boot:run
   ```
3. Service is available at `http://localhost:8081`, Swagger UI at
   `http://localhost:8081/swagger-ui.html`.

## Tests

```bash
cd manpower-planning-service
mvn test
```
Tests run against an in-memory H2 database (`test` profile) and don't require Docker.

Requires JDK 21 — if `mvn -version` reports a different major version, set `JAVA_HOME`
to a JDK 21 install before running.
