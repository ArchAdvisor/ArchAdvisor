# ArchAdvisor

Project for university master course Software Design

## Description

ArchAdvisor is a decision-support system that helps software engineers, students, and teams evaluate and compare
technology stacks based on project requirements, team capabilities, and weighted priority criteria.

The backend is implemented in Java + Spring Boot, and PostgreSQL is managed using Docker Compose.
The frontend is implemented in React and TypeScript (Vite)

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

## License

MIT License
