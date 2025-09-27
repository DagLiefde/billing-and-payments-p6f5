# Billing and Payments API - JWT Authentication System

This project implements a secure backend authentication system using JWT (JSON Web Tokens) following SOLID principles.

## 🏗️ Architecture Overview

The authentication system is built following SOLID principles:

- **Single Responsibility Principle (SRP)**: Each class has one reason to change
- **Open/Closed Principle (OCP)**: Open for extension, closed for modification
- **Liskov Substitution Principle (LSP)**: Derived classes are substitutable for base classes
- **Interface Segregation Principle (ISP)**: Clients shouldn't depend on interfaces they don't use
- **Dependency Inversion Principle (DIP)**: Depend on abstractions, not concretions

## 🚀 Features

- JWT-based authentication
- User registration and login
- Password encryption using BCrypt
- Role-based access control (USER, ADMIN)
- Secure API endpoints
- Database integration with PostgreSQL
- Comprehensive error handling
- RESTful API design

## 📁 Project Structure

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
```

## 🛠️ Setup Instructions

### 1. Database Configuration

Update the database connection in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://your-neon-database-url
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. JWT Configuration

The JWT secret and expiration are configured in `application.properties`:

```properties
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000  # 24 hours in milliseconds
```

### 3. Running the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 📚 API Endpoints

### Authentication Endpoints

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

### User Management Endpoints

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
```http
DELETE /api/users/{id}
Authorization: Bearer <jwt_token>
```

## 🔐 Security Features

- **Password Encryption**: BCrypt password hashing
- **JWT Tokens**: Secure token-based authentication
- **Role-based Access**: USER and ADMIN roles
- **CORS Configuration**: Cross-origin resource sharing
- **Input Validation**: Request validation using Bean Validation
- **Error Handling**: Comprehensive error responses

## 🧪 Testing the API

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

## 🔧 Configuration

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

For production, consider using environment variables:

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

## 📝 SOLID Principles Implementation

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

The application includes comprehensive logging:

- DEBUG level for application packages
- Security event logging
- SQL query logging (in development)

## 🚀 Deployment

1. Update database configuration for production
2. Set secure JWT secret
3. Configure CORS for your frontend domain
4. Set up SSL/TLS for HTTPS
5. Configure proper logging levels

## 📞 Support

For issues or questions, please check the logs and ensure all dependencies are properly configured.
