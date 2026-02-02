![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

![Project Status](https://img.shields.io/badge/Status-Prototype-yellow?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)
# ArchAdvisor

Project for university master course Software Design

## Description

ArchAdvisor is a decision-support system that helps software engineers, students, and teams evaluate and compare
technology stacks based on project requirements, team capabilities, and weighted priority criteria.

## Features (MVP)
- Questionnaire-based project profiling
- Rule- and metric-based technology recommendations
- Support for backend-only, full-stack, and mobile scopes
- PostgreSQL-backed technology catalog

## Architecture
- Spring Boot (Java)
- Layered architecture (API, service, persistence)
- PostgreSQL (via Docker Compose)
- Frontend (React +Vite)

## Prerequisites

Before running the backend, ensure you have:

- Java 21 (or the version defined in your pom.xml)
- Maven 3.x
- Docker + Docker Compose
- Node.js ≥ 18
- npm or pnpm
- Git

### Start PostgreSQL via Docker Compose

In the project root, start the database:

```bash
docker compose up -d
```

This launches a PostgreSQL database running on:
Host: localhost
Port: 5432
Database: archadvisor
User: postgres
Password: postgres
(adjust these in application.yml if needed)

Verify that it is running:

```bash
docker ps
```

### Run the Spring Boot Backend

Navigate to the backend module:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/archadvisor-0.0.1-SNAPSHOT.jar
```

The main API endpoint will be available at:

http://localhost:8080/api/recommendations

### Run the Frontend

```bash
cd frontend
npm install
```

Start the frontend

````bash
npm run dev
````
Access it at:
http://localhost:3000

### Project status
This is an academic prototype. The recommendation logic and metrics are intentionally simplified.
