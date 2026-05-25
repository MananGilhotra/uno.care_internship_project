# 🚀 ConfigVault Deployment Guide

This guide details how to deploy the ConfigVault application into a production environment. ConfigVault consists of a React frontend and a Spring Boot backend. 

For production, the recommended approach is to build the React application as static files and serve them directly from the Spring Boot backend, resulting in a single deployable `.jar` file.

---

## 📋 Prerequisites

Before deploying, ensure the target server has the following installed:
- **Java 17** (or higher)
- **Node.js 18+** & **npm** (for building the frontend)
- **MySQL 8.0+** (Database)

---

## 🛠️ Step 1: Build the Frontend

First, build the React frontend for production.

```bash
# Navigate to the frontend directory
cd configvault-ui

# Install dependencies
npm install

# Build the production optimized files
npm run build
```

This will create a `dist/` directory containing your HTML, CSS, and JS files.

---

## 📦 Step 2: Package the Backend

Next, we need to copy the built frontend files into the Spring Boot backend's public directory so that Spring Boot can serve them, and then compile the JAR.

```bash
# Clear any old static files from the backend
rm -rf ../configvault-backend/src/main/resources/public/*

# Copy the new frontend build to the backend
cp -r dist/* ../configvault-backend/src/main/resources/public/

# Navigate to the backend directory
cd ../configvault-backend

# Build the Spring Boot executable JAR (skipping tests for deployment)
mvn clean package -DskipTests
```

This will generate an executable JAR file at `configvault-backend/target/configvault-1.0.0.jar`.

---

## 🗄️ Step 3: Database Setup

1. Log into your MySQL server.
2. Create a dedicated database for ConfigVault:
   ```sql
   CREATE DATABASE configvault_db;
   ```
3. (Optional) Run the schema generation script located in `sql/schema.sql` to seed the database with initial categories and properties. *Note: Spring Boot's Hibernate will automatically create the tables if they don't exist.*

---

## 🚀 Step 4: Run the Application

You can now run the compiled JAR file on your server. It is highly recommended to use environment variables to configure your production database credentials.

```bash
# Set environment variables for production
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/configvault_db
export SPRING_DATASOURCE_USERNAME=your_db_username
export SPRING_DATASOURCE_PASSWORD=your_db_secure_password

# Start the application
java -jar target/configvault-1.0.0.jar
```

> [!TIP]
> **Running as a Background Service**
> In a real production environment, you should run the JAR file using a process manager like **systemd** (Linux) or containerize it using Docker so that it restarts automatically on failure.

---

## 🐳 Alternative: Docker Deployment

If you prefer to deploy using Docker, you can create a `Dockerfile` and `docker-compose.yml` to automate the build and deployment process.

### 1. Create a `Dockerfile` in the project root:

```dockerfile
# Stage 1: Build Frontend
FROM node:18-alpine AS frontend-builder
WORKDIR /app/ui
COPY configvault-ui/package*.json ./
RUN npm ci
COPY configvault-ui/ ./
RUN npm run build

# Stage 2: Build Backend
FROM maven:3.9.6-eclipse-temurin-17 AS backend-builder
WORKDIR /app/backend
COPY configvault-backend/pom.xml .
COPY configvault-backend/src ./src
# Copy frontend build to backend public folder
COPY --from=frontend-builder /app/ui/dist ./src/main/resources/public
RUN mvn clean package -DskipTests

# Stage 3: Run Application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/backend/target/configvault-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Create a `docker-compose.yml` in the project root:

```yaml
version: '3.8'

services:
  configvault-db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: configvault_db
      MYSQL_ROOT_PASSWORD: secure_root_password
      MYSQL_USER: cv_user
      MYSQL_PASSWORD: cv_password
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql
      - ./sql/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  configvault-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://configvault-db:3306/configvault_db
      - SPRING_DATASOURCE_USERNAME=cv_user
      - SPRING_DATASOURCE_PASSWORD=cv_password
    depends_on:
      - configvault-db

volumes:
  db_data:
```

### 3. Deploy with Docker Compose:

```bash
docker-compose up -d --build
```
