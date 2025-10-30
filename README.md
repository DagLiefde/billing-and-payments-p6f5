# Billing and Payments Backend – Microservices for Billing Module

This repository contains the existing authentication service and three new Spring Boot microservices to implement the Billing module (invoices, shipments, documents) with PostgreSQL (Neon), Docker, Kubernetes, and CI/CD.

## 🏗️ Architecture Overview

Microservices (bounded contexts):
- user-auth-service (root app): JWT auth, users, accessibility preferences endpoints
- invoice-service: invoice draft/edit/issue lifecycle, history, idempotency
- shipment-service: shipments listing/detail for invoice prefill
- document-service: upload/download attachments

Stack: Spring Boot 3 (Java 21), Spring Data JPA, Flyway, PostgreSQL (Neon), Spring Security, Actuator (Prometheus), springdoc-openapi, Docker, Kubernetes, GitHub Actions, SonarCloud.

## 🚀 Features (per User Stories)

- HU-002 Create Invoice: create draft, multiple shipments, prevent duplicates, validate fields, prefill via shipment-service, offline draft design
- HU-003 Edit Draft: OCC via @Version, history versions, revert design, attachments via document-service
- HU-004 Issue Invoice: fiscal validations, unique folio, idempotent issuance, lock edits after issuing, audit trail
- HU-010 Accessibility: backend endpoints for user preferences

## 📁 Project Structure (selected)

```
src/main/java/com/fabrica/p6f5/springapp/
├── config/
│   └── SecurityConfig.java          # Security configuration
├── controller/
│   ├── AuthController.java          # Authentication endpoints
│   └── UserController.java          # User management endpoints
├── dto/
│   ├── ApiResponse.java             # Generic API response
│   ├── AuthResponse.java            # Authentication response
│   ├── LoginRequest.java            # Login request DTO
│   └── RegisterRequest.java        # Registration request DTO
├── entity/
│   └── User.java                    # User entity with UserDetails
├── repository/
│   └── UserRepository.java          # User data access interface
├── security/
│   ├── JwtAuthenticationEntryPoint.java  # Authentication entry point
│   └── JwtAuthenticationFilter.java      # JWT filter
├── service/
│   ├── AuthService.java             # Authentication business logic
│   ├── JwtService.java              # JWT token operations
│   └── UserService.java             # User management service
└── SpringappApplication.java        # Main application class

services/
  invoice-service/                   # Spring Boot microservice (port 8081)
  shipment-service/                  # Spring Boot microservice (port 8082)
  document-service/                  # Spring Boot microservice (port 8083)
k8s/                                 # K8s manifests per service + secrets template
.github/workflows/ci.yml             # CI (build, test, SonarCloud, push images)
docker-compose.yml                   # Local multi-service run
monitoring/grafana-dashboard-example.json
```

## 🛠️ Setup & Run (Local)

### 1. Database Configuration

Neon connections (preserve existing root app config). For new services set env vars:
- INVOICE_DB_URL/USERNAME/PASSWORD
- SHIPMENT_DB_URL/USERNAME/PASSWORD
- DOCUMENT_DB_URL/USERNAME/PASSWORD
Use `sslmode=require` in JDBC URLs.

### 2. JWT Configuration

The JWT secret and expiration are configured in `application.properties`:

```properties
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000  # 24 hours in milliseconds
```

### 3. Running the Application

Root auth service:
```bash
./gradlew bootRun
```
New services via Docker Compose:
```bash
docker-compose up --build
```
Ports: auth 8080, invoice 8081, shipment 8082, document 8083.

## 📚 API Endpoints

### Authentication & Users (root)

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "password123"
}
```

#### Get User Profile
```http
GET /api/auth/profile
Authorization: Bearer <jwt_token>
```

#### Validate Token
```http
POST /api/auth/validate?token=<jwt_token>
```

### User Management & Accessibility

#### Get All Users (Admin only)
```http
GET /api/users
Authorization: Bearer <jwt_token>
```

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer <jwt_token>
```

#### Update User
```http
PUT /api/users/{id}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "username": "updated_username",
  "email": "updated@example.com"
}
```

#### Delete User (Admin only)

#### Accessibility Preferences (HU-010)
```http
GET /api/users/{id}/preferences
PUT /api/users/{id}/preferences
Content-Type: application/json

{
  "font_size": "md|lg",
  "contrast_mode": "light|dark|high"
}
```

### Shipment-service
```http
GET /api/v1/shipments?status=CREATED|DELIVERED|CANCELLED
GET /api/v1/shipments/{id}
```

### Invoice-service
```http
POST /api/v1/invoices            # Headers: X-User-Id
GET  /api/v1/invoices/{id}
PUT  /api/v1/invoices/{id}       # OCC, Headers: X-User-Id
POST /api/v1/invoices/{id}/issue # Idempotent, Headers: X-User-Id
GET  /api/v1/invoices/{id}/history
```

### Document-service
```http
POST /api/v1/documents           # multipart/form-data, Headers: X-User-Id
GET  /api/v1/documents/{id}
```
```http
DELETE /api/users/{id}
Authorization: Bearer <jwt_token>
```

## 🔐 Security & RBAC

- JWT existing in auth-service (preserved). Recommended: API gateway for central validation across services.
- Roles: finance_operator (create/edit/issue), auditor (read), admin (all).
- Input validation with Bean Validation (@Valid records/DTOs). Structured error responses.

## 🧪 Testing the API (examples)

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'
```

### 3. Access protected endpoint
```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer <your_jwt_token>"
```

## 🔧 Configuration (Neon & Secrets)

### Database Schema

The application will automatically create the following table:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_enabled BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE
);
```

### Environment Variables

For production, set environment variables (Kubernetes secrets recommended):

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
jwt.secret=${JWT_SECRET}
```

## 🚨 Error Handling

The API returns consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": 1234567890
}
```

## 📝 Clean Architecture & SOLID

### Single Responsibility Principle
- `User` entity: Only handles user data
- `JwtService`: Only handles JWT operations
- `AuthService`: Only handles authentication logic

### Open/Closed Principle
- Controllers are open for extension (new endpoints) but closed for modification
- Services can be extended without modifying existing code

### Liskov Substitution Principle
- `User` implements `UserDetails` and can be substituted anywhere `UserDetails` is expected

### Interface Segregation Principle
- `UserRepository` only contains user-related methods
- Services have focused interfaces

### Dependency Inversion Principle
- Controllers depend on service interfaces, not implementations
- Services depend on repository interfaces, not concrete implementations

## 🔍 Monitoring and Logging

- Actuator Prometheus endpoint enabled in new services; sample Grafana dashboard in `monitoring/grafana-dashboard-example.json`.

## 🚀 Deployment

1. Build images via GitHub Actions (GHCR push). Or locally: `docker build` per service.
2. Kubernetes: create Neon secrets and apply manifests in `k8s/`.
3. Verify readiness/liveness probes; access Swagger UI per service.
4. SonarCloud: create organization and project keys; set tokens as repo secrets.

## 📞 Support

For issues or questions, please check the logs and ensure all dependencies are properly configured.
