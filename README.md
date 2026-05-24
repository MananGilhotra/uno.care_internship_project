<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3.2"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL 8.0"/>
  <img src="https://img.shields.io/badge/Maven-3.8-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge" alt="License"/>
  <img src="https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge" alt="Build"/>
</p>

---

# 🔐 ConfigVault

> **A centralized configuration management REST API** — built with Spring Boot, designed for modern enterprise applications.

---

## 📖 Overview

**ConfigVault** is a production-grade REST API for managing application configuration properties in a centralized key-value store. It provides a single source of truth for configuration data across services, environments, and teams.

Built with **Java 17+** and **Spring Boot 3.2**, ConfigVault demonstrates enterprise-level patterns including layered architecture, DTO mapping, soft-delete patterns, pagination, input validation, and sensitive data masking. The API automatically masks values for restricted keys (like `JWT_SECRET`, `DB_PASSWORD`, etc.), preventing accidental exposure of secrets through API responses.

Whether you're managing feature flags, environment-specific settings, or security credentials, ConfigVault offers a clean, documented API with Swagger UI, comprehensive test coverage, and Docker-ready deployment.

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🔑 **CRUD Operations** | Create, read, update, and soft-delete configuration properties |
| 🔒 **Restricted Key Protection** | Automatically blocks modification/deletion of sensitive keys |
| 🎭 **Value Masking** | Masks values of restricted keys (e.g., `JWT_SECRET` → `********`) |
| 📄 **Pagination** | Server-side pagination with configurable page size |
| 🏷️ **Category Filtering** | Group and filter properties by category |
| ✅ **Input Validation** | Bean Validation with descriptive error messages |
| 🗑️ **Soft Delete** | Logical deletion preserving audit trail |
| 📊 **Swagger UI** | Interactive API documentation via SpringDoc OpenAPI |
| 🐳 **Docker Ready** | Multi-stage Dockerfile + Docker Compose with MySQL |
| 🧪 **Comprehensive Tests** | Unit, integration, and web-layer tests |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|:-----------|:--------|
| **Java 17+** | Programming language |
| **Spring Boot 3.2.12** | Application framework |
| **Spring Data JPA** | Data access & ORM |
| **Hibernate** | JPA implementation |
| **MySQL 8.0** | Production database |
| **H2 Database** | In-memory testing database |
| **Maven** | Build tool & dependency management |
| **SpringDoc OpenAPI** | Swagger UI / API docs |
| **JUnit 5** | Testing framework |
| **Mockito** | Mocking framework |
| **Docker** | Containerization |
| **Lombok** | Boilerplate reduction |

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────┐
│                     CLIENT (Postman / Browser)            │
└──────────────┬───────────────────────────────────────────┘
               │  HTTP Request
┌──────────────▼───────────────────────────────────────────┐
│              PropertyController  (@RestController)        │
│              ├── POST   /api/v1/properties               │
│              ├── GET    /api/v1/properties/{key}          │
│              ├── GET    /api/v1/properties                │
│              ├── GET    /api/v1/properties/category/{cat} │
│              ├── DELETE /api/v1/properties/{key}          │
│              └── GET    /api/v1/properties/categories     │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│              PropertyService  (Interface)                 │
│              └── PropertyServiceImpl  (Business Logic)    │
│                  ├── MaskingUtil  (Value masking)         │
│                  └── PropertyMapper (Entity ↔ DTO)        │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│              PropertyRepository  (JPA Repository)         │
│              └── Custom queries (findByKey, findActive)    │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│              MySQL Database  (configvault_db)              │
│              └── properties table                         │
└──────────────────────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (JDK 17 or higher)
- **Maven 3.6+**
- **MySQL 8.0** (or use Docker)
- **Docker & Docker Compose** (optional)

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/MananGilhotra/uno.care_internship_project.git
cd uno.care_internship_project
```

### 2️⃣ Database Setup

**Option A: Local MySQL**

```sql
CREATE DATABASE configvault_db;
```

Then run the seed script:

```bash
mysql -u root -p configvault_db < sql/schema.sql
```

**Option B: Docker (Recommended)**

```bash
docker-compose up -d mysql_db
```

### 3️⃣ Configure Application

Update `src/main/resources/application-dev.yml` with your database credentials, or use environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/configvault_db
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=root
```

### 4️⃣ Build & Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at: **http://localhost:8080**

---

## 📡 API Endpoints

| Method | Endpoint | Description | Status |
|:------:|:---------|:------------|:------:|
| `POST` | `/api/v1/properties` | Create or update a property | `201` |
| `GET` | `/api/v1/properties/{key}` | Get a property by key | `200` |
| `GET` | `/api/v1/properties` | List all active properties (paginated) | `200` |
| `GET` | `/api/v1/properties/category/{category}` | Filter properties by category | `200` |
| `DELETE` | `/api/v1/properties/{key}` | Soft-delete a property | `200` |
| `GET` | `/api/v1/properties/categories` | List all distinct categories | `200` |
| `GET` | `/actuator/health` | Application health check | `200` |

---

## 💡 API Examples

### Create a Property

```bash
curl -X POST http://localhost:8080/api/v1/properties \
  -H "Content-Type: application/json" \
  -d '{
    "key": "APP_NAME",
    "value": "ConfigVault",
    "category": "APPLICATION"
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Property created successfully",
  "data": {
    "id": 1,
    "key": "APP_NAME",
    "value": "ConfigVault",
    "category": "APPLICATION",
    "isActive": true,
    "isRestricted": false,
    "createdDate": "2026-01-01T00:00:00",
    "lastModifiedDate": "2026-01-01T00:00:00"
  },
  "status": 201,
  "timestamp": "2026-01-01T00:00:00"
}
```

### Get a Property

```bash
curl http://localhost:8080/api/v1/properties/APP_NAME
```

### Get a Restricted Key (Masked Value)

```bash
curl http://localhost:8080/api/v1/properties/JWT_SECRET
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "key": "JWT_SECRET",
    "value": "********",
    "category": "SECURITY",
    "isRestricted": true
  }
}
```

### List Properties with Pagination

```bash
curl "http://localhost:8080/api/v1/properties?page=0&size=5"
```

### Filter by Category

```bash
curl "http://localhost:8080/api/v1/properties/category/SECURITY?page=0&size=10"
```

### Soft Delete a Property

```bash
curl -X DELETE http://localhost:8080/api/v1/properties/LOG_LEVEL
```

### Try Deleting a Restricted Key (403 Forbidden)

```bash
curl -X DELETE http://localhost:8080/api/v1/properties/JWT_SECRET
```

**Response (403 Forbidden):**
```json
{
  "success": false,
  "message": "The key 'JWT_SECRET' is restricted and cannot be modified or deleted",
  "status": 403
}
```

### Get All Categories

```bash
curl http://localhost:8080/api/v1/properties/categories
```

---

## 📚 Swagger UI

Once the application is running, access the interactive API documentation at:

🔗 **http://localhost:8080/swagger-ui.html**

Swagger provides:
- Interactive request/response testing
- Auto-generated API documentation
- Schema definitions for all DTOs

<!-- 
📸 Screenshot placeholder:
Add a screenshot of your Swagger UI here.
![Swagger UI](docs/screenshots/swagger-ui.png)
-->

---

## 🐳 Docker Setup

### Run with Docker Compose (Recommended)

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Build Docker Image Manually

```bash
# Build the image
docker build -t configvault:latest .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/configvault_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  configvault:latest
```

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Classes

```bash
# Unit tests
mvn test -Dtest=PropertyServiceImplTest

# Controller tests
mvn test -Dtest=PropertyControllerTest

# Repository tests
mvn test -Dtest=PropertyRepositoryTest

# Utility tests
mvn test -Dtest=MaskingUtilTest
```

### Test Coverage

| Layer | Test Class | Tests | Type |
|:------|:-----------|:-----:|:----:|
| Service | `PropertyServiceImplTest` | 12 | Unit |
| Controller | `PropertyControllerTest` | 8 | Web MVC |
| Repository | `PropertyRepositoryTest` | 7 | Integration |
| Utility | `MaskingUtilTest` | 7 | Unit |
| Application | `ConfigVaultApplicationTests` | 1 | Smoke |
| **Total** | | **35** | |

---

## ⚙️ Configuration

### Profiles

| Profile | Database | Purpose |
|:--------|:---------|:--------|
| `dev` | MySQL (local) | Local development |
| `test` | H2 (in-memory) | Automated testing |
| `prod` | MySQL (production) | Production deployment |

### Environment Variables

| Variable | Default | Description |
|:---------|:--------|:------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/configvault_db` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `root` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `root` | Database password |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile |
| `SERVER_PORT` | `8080` | Application port |

---

## 📂 Project Structure

```
configvault/
├── 📄 pom.xml                          # Maven configuration
├── 🐳 Dockerfile                       # Multi-stage Docker build
├── 🐳 docker-compose.yml               # Docker Compose orchestration
├── 📖 README.md                        # This file
├── 📄 .gitignore                       # Git ignore rules
│
├── 📁 sql/
│   └── 📄 schema.sql                   # Database schema + seed data
│
├── 📁 postman/
│   └── 📄 ConfigVault_API.postman_collection.json
│
└── 📁 src/
    ├── 📁 main/
    │   ├── 📁 java/com/configvault/
    │   │   ├── 📄 ConfigVaultApplication.java      # Main entry point
    │   │   ├── 📁 config/                           # Configuration classes
    │   │   ├── 📁 constants/
    │   │   │   └── 📄 AppConstants.java             # Application constants
    │   │   ├── 📁 controller/
    │   │   │   └── 📄 PropertyController.java       # REST controller
    │   │   ├── 📁 dto/
    │   │   │   ├── 📁 request/
    │   │   │   │   └── 📄 PropertyRequest.java      # Request DTO
    │   │   │   └── 📁 response/
    │   │   │       ├── 📄 ApiResponse.java          # Generic API response
    │   │   │       ├── 📄 PagedResponse.java        # Paginated response
    │   │   │       └── 📄 PropertyResponse.java     # Response DTO
    │   │   ├── 📁 entity/
    │   │   │   └── 📄 Property.java                 # JPA entity
    │   │   ├── 📁 exception/
    │   │   │   ├── 📄 DuplicateKeyException.java
    │   │   │   ├── 📄 GlobalExceptionHandler.java
    │   │   │   ├── 📄 RateLimitExceededException.java
    │   │   │   ├── 📄 ResourceNotFoundException.java
    │   │   │   └── 📄 RestrictedKeyException.java
    │   │   ├── 📁 mapper/
    │   │   │   └── 📄 PropertyMapper.java           # Entity ↔ DTO mapper
    │   │   ├── 📁 repository/
    │   │   │   └── 📄 PropertyRepository.java       # JPA repository
    │   │   ├── 📁 service/
    │   │   │   ├── 📄 PropertyService.java          # Service interface
    │   │   │   └── 📁 impl/
    │   │   │       └── 📄 PropertyServiceImpl.java  # Service implementation
    │   │   └── 📁 util/
    │   │       └── 📄 MaskingUtil.java              # Value masking utility
    │   └── 📁 resources/
    │       ├── 📄 application.yml                   # Base config
    │       ├── 📄 application-dev.yml               # Dev profile
    │       └── 📄 application-test.yml              # Test profile
    │
    └── 📁 test/
        └── 📁 java/com/configvault/
            ├── 📄 ConfigVaultApplicationTests.java  # Context load test
            ├── 📁 controller/
            │   └── 📄 PropertyControllerTest.java   # Web MVC tests
            ├── 📁 repository/
            │   └── 📄 PropertyRepositoryTest.java   # JPA tests
            ├── 📁 service/
            │   └── 📄 PropertyServiceImplTest.java  # Service unit tests
            └── 📁 util/
                └── 📄 MaskingUtilTest.java          # Utility tests
```

---

## 🎯 Key Design Decisions

| Decision | Rationale |
|:---------|:----------|
| **Layered Architecture** | Clear separation of concerns: Controller → Service → Repository |
| **DTO Pattern** | Decouples API contract from database entity; allows independent evolution |
| **Soft Delete** | Preserves historical data for auditing; `is_active` flag instead of physical deletion |
| **Restricted Keys** | Prevents accidental modification of critical security configurations |
| **Value Masking** | Security-by-default: sensitive values are never exposed through the API |
| **Upsert Pattern** | Single POST endpoint handles both create and update, reducing API complexity |
| **Case-Insensitive Keys** | Prevents duplicate entries like `APP_NAME` and `app_name` |
| **Pagination** | Prevents large result sets from degrading performance |
| **Profile-Based Config** | Different databases for dev/test/prod; H2 for testing eliminates MySQL dependency |

---

## 📸 Screenshots

<!-- 
Add your screenshots here:

### Swagger UI
![Swagger UI](docs/screenshots/swagger-ui.png)

### Create Property
![Create Property](docs/screenshots/create-property.png)

### Masked Value
![Masked Value](docs/screenshots/masked-value.png)

### Postman Collection
![Postman](docs/screenshots/postman.png)
-->

> 📸 *Screenshots coming soon! Run the project and capture them.*

---

## 📝 Resume Description

Use these bullet points on your resume:

> **ConfigVault — Centralized Configuration Management API** *(Java 17+, Spring Boot 3.2, MySQL)*
>
> - Designed and built a **RESTful API** for centralized configuration management with **CRUD operations**, **pagination**, **category filtering**, and **soft-delete** functionality
> - Implemented **sensitive data masking** for restricted keys (JWT secrets, passwords) with automatic detection and `********` value replacement in API responses
> - Built **35+ automated tests** across unit, integration, and web-layer testing using **JUnit 5**, **Mockito**, and **MockMvc** achieving comprehensive code coverage
> - Containerized the application with a **multi-stage Docker build** and **Docker Compose** orchestrating Spring Boot + MySQL with health checks
> - Applied enterprise design patterns: **DTO mapping**, **service-repository layering**, **global exception handling**, **Bean Validation**, and **API versioning** (`/api/v1/`)
> - Documented the API with **Swagger UI (SpringDoc OpenAPI)** and provided a complete **Postman collection** with 11 pre-configured requests

---

## 📚 Key Learnings

Building ConfigVault provided hands-on experience with:

- 🏗️ **Layered Architecture** — Structuring code for maintainability and testability
- 🔄 **Spring Data JPA** — Custom query methods, pagination, and auditing
- 🛡️ **Exception Handling** — Global exception handler with consistent API responses
- ✅ **Testing Strategies** — Unit testing with Mockito, `@WebMvcTest` for controllers, `@DataJpaTest` for repositories
- 🐳 **Docker** — Multi-stage builds, Docker Compose, health checks, and networking
- 📊 **API Design** — RESTful conventions, pagination patterns, and versioning
- 🔒 **Security Patterns** — Sensitive data masking without full authentication overhead

---

## 🤝 Contributing

Contributions are welcome! Here's how:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

Please make sure to:
- Write tests for new features
- Follow existing code style and conventions
- Update documentation as needed

---

## 📄 License

This project is licensed under the **Apache License 2.0** — see the [LICENSE](LICENSE) file for details.

```
Copyright 2026 ConfigVault

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

---

## 👤 Author

<!-- Replace with your information -->
- **Name**: Manan Gilhotra
- **GitHub**: [@MananGilhotra](https://github.com/MananGilhotra)
- **LinkedIn**: [Manan Gilhotra](https://linkedin.com/in/manangilhotra)

---

<p align="center">
  Made with ❤️ and ☕ using Spring Boot
  <br/>
  ⭐ Star this repo if you found it helpful!
</p>
