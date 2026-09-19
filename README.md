
# HealthWealth Backend

HealthWealth is a Spring Boot REST API for managing patient health profiles and generating hospital recommendations based on diseases, hospital availability, location, ratings, emergency support, insurance coverage, and eligible government schemes.

## Project Status

Core APIs are implemented and manually tested with Postman, including patient CRUD, patient-disease management, and disease-specific hospital recommendations.

## Tech Stack

- Java 23
- Spring Boot 4.0.8
- Spring Web / REST
- Spring JDBC
- MySQL
- HikariCP
- Jakarta Bean Validation
- Maven
- Postman

## Architecture

```text
Client / Postman
       |
       v
Controller
       |
       v
Service
       |
       v
DAO
       |
       v
MySQL
```

### Package Structure

```text
com.healthwealth
├── controller
├── service
├── dao
├── dto
├── model
├── exception
└── HealthWealthApplication.java
```

- **Controller:** REST endpoints and HTTP request/response handling.
- **Service:** Business logic and recommendation calculations.
- **DAO:** JDBC-based database access.
- **DTO:** API request/response objects.
- **Model:** Domain/database objects.
- **Exception:** Custom exceptions and centralized error handling.

## Database

Create the database:

```sql
CREATE DATABASE healthwealth;
```

Main tables:

```text
patients
diseases
patient_diseases
insurance_policies
patient_insurances
policy_diseases
government_schemes
scheme_diseases
hospitals
hospital_services
```

The SQL initialization scripts create the required schema and demo data.

## Configuration

Example configuration:

```properties
spring.application.name=healthwealth-backend
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/healthwealth?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD:root}

spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000

spring.sql.init.mode=always
spring.sql.init.encoding=UTF-8
spring.jackson.default-property-inclusion=non_null
```

**Do not commit a real database password to GitHub.** Prefer an environment variable:

```text
DB_PASSWORD=your_mysql_password
```

On Windows PowerShell:

```powershell
$env:DB_PASSWORD="your_mysql_password"
```

## Running the Application

### Prerequisites

- JDK 23
- MySQL Server
- Maven
- Postman

### Start MySQL

Make sure MySQL Server is running.

### Create the database

```sql
CREATE DATABASE healthwealth;
```

### Start Spring Boot

```bash
mvn clean spring-boot:run
```

Or run `HealthWealthApplication.java` from IntelliJ IDEA.

The API runs at:

```text
http://localhost:8080
```

# REST API

Base URL:

```text
http://localhost:8080/api
```

## Patient APIs

### Create Patient

```http
POST /api/patients
```

Postman: **Body → raw → JSON**

```json
{
  "identityRef": "DEMO-IDENTITY-2001",
  "fullName": "Test Patient",
  "dateOfBirth": "1995-04-10",
  "gender": "MALE",
  "city": "Pune",
  "annualIncome": 400000,
  "phone": "9000000010",
  "bloodGroup": "O+",
  "emergencyContact": "9111111120"
}
```

Expected:

```text
201 Created
```

### Get All Patients

```http
GET /api/patients
```

Optional city filter:

```http
GET /api/patients?city=Pune
```

Pagination:

```http
GET /api/patients?page=0&size=10
```

### Get Patient

```http
GET /api/patients/{id}
```

Example:

```http
GET /api/patients/1
```

Expected: `200 OK`

### Update Patient

```http
PUT /api/patients/{id}
```

Example:

```http
PUT /api/patients/1
```

Body → raw → JSON:

```json
{
  "identityRef": "DEMO-IDENTITY-1001",
  "fullName": "Rahul Patil",
  "dateOfBirth": "1974-05-12",
  "gender": "MALE",
  "city": "Pune",
  "annualIncome": 350000,
  "phone": "9000000001",
  "bloodGroup": "B+",
  "emergencyContact": "9111111111"
}
```

Expected: `200 OK`

### Delete Patient

```http
DELETE /api/patients/{id}
```

Example:

```http
DELETE /api/patients/1
```

Expected: `204 No Content`

# Patient Disease APIs

### Get Patient Diseases

```http
GET /api/patients/{id}/diseases
```

Example:

```http
GET /api/patients/1/diseases
```

Expected: `200 OK`

### Add Disease to Patient

```http
POST /api/patients/{patientId}/diseases/{diseaseId}
```

Example:

```http
POST /api/patients/2/diseases/1
```

No request body is required.

Expected for a new relationship:

```text
201 Created
```

If the relationship already exists, the API returns `400 Bad Request`. This is expected because:

```sql
PRIMARY KEY (patient_id, disease_id)
```

prevents duplicate patient-disease relationships.

# Recommendation API

The main business feature is:

```http
GET /api/recommendations/{patientId}
```

Optional disease:

```http
GET /api/recommendations/{patientId}?diseaseId={diseaseId}
```

### Default Disease Recommendation

```http
GET /api/recommendations/1
```

The service selects a disease from the patient's profile.

### Specific Disease Recommendation

```http
GET /api/recommendations/1?diseaseId=2
```

The specified disease must belong to the patient's profile.

### Verified Recommendation Tests

| Test | Request | Expected |
|---|---|---|
| A | `GET /api/recommendations/1` | 200 + Diabetes recommendations |
| B | `GET /api/recommendations/1?diseaseId=2` | 200 + Hypertension recommendations |
| C | `GET /api/recommendations/2?diseaseId=4` | 200 + Asthma recommendations |
| D | `GET /api/recommendations/2?diseaseId=1` | 400 — Disease not in patient profile |

## Recommendation Algorithm

For each hospital service matching the selected disease:

### Insurance Contribution

```text
estimated cost × insurance coverage percentage / 100
```

### Government Scheme Contribution

```text
minimum(estimated cost, best eligible scheme coverage)
```

### Out-of-Pocket Cost

```text
estimated cost
- insurance contribution
- scheme contribution
```

The calculated out-of-pocket amount is never allowed to become negative.

### Recommendation Score

Current demo scoring:

- Available service: **+30**
- Same city as patient: **+20**
- Hospital rating: **rating × 6**, up to 30 points
- Emergency service: **+10**
- Financial assistance reduces cost: **+10**

The final score is capped at 100.

Recommendations are sorted by:

1. Highest recommendation score
2. Lowest estimated out-of-pocket cost

# Error Handling

`GlobalExceptionHandler` provides centralized REST error responses.

### 404

```json
{
  "timestamp": "2026-09-13T08:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Patient not found with id: 999",
  "path": "/api/patients/999"
}
```

### 400

```json
{
  "timestamp": "2026-09-13T08:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Disease is not part of this patient profile.",
  "path": "/api/recommendations/2"
}
```

Bean Validation is applied through `@Valid`.

# Postman Testing

For POST and PUT requests:

```text
Body → raw → JSON
```

Use:

```http
Content-Type: application/json
```

GET requests normally require no request body.

# Design Decisions

## Why Spring Boot?

Spring Boot provides REST support, dependency injection, embedded Tomcat, validation, configuration, and exception handling.

## Why JDBC?

Spring JDBC was used intentionally to demonstrate SQL and database fundamentals, including PreparedStatements, connection handling, ResultSet mapping, and the DAO pattern.

## Why DTOs?

DTOs keep API request/response contracts separate from database/domain models.

Examples:

```text
PatientRequest
PatientResponse
RecommendationResponse
```

# OOP Concepts Used

### Encapsulation

Model classes encapsulate fields through private variables and getters/setters.

### Abstraction

Service and DAO layers hide implementation details from controllers.

### Dependency Injection

Spring injects dependencies through constructors.

### Separation of Concerns

```text
Controller → HTTP/API
Service    → Business Logic
DAO        → Database
DTO        → API Data
Model      → Domain Data
Exception  → Error Handling
```

# Security Note

Never commit real credentials.

Prefer:

```properties
spring.datasource.password=${DB_PASSWORD}
```

with the secret supplied through the environment.

Production deployment should additionally consider authentication, authorization, HTTPS, secret management, audit logging, rate limiting, and production database configuration.

# Current Status

- [x] Spring Boot application
- [x] MySQL integration
- [x] Patient CRUD APIs
- [x] Patient disease APIs
- [x] DTO request/response handling
- [x] Bean Validation
- [x] Centralized exception handling
- [x] Hospital/service data
- [x] Insurance data
- [x] Government scheme data
- [x] Recommendation engine
- [x] Recommendation scoring
- [x] Postman testing
- [x] Disease-specific recommendation validation
- [x] Layered package architecture


This project is intended as a learning/demo backend application.
