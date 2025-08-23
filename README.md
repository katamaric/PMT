# PMT - Project Management Tool

PMT is a web-based project management tool that allows users to register, log in, create projects, assign tasks, and collaborate effectively. It is built with a Java Spring Boot backend and an Angular frontend.

---

## Features

- User register and login
- Project and task management
- Project invitations and team collaboration
- Full backend and frontend test coverage
- Dockerized microservices
- CI/CD pipeline with GitHub Actions

---

## Tech Stack

**Frontend:** Angular + TypeScript  
**Backend:** Spring Boot (Java 17) + PostgreSQL  
**Database:** PostgreSQL  
**CI/CD:** GitHub Actions  
**Testing:**

- Backend: JUnit + Mockito
- Frontend: Karma + Jasmine  
  **Docker:** Docker, Docker Compose

---

## Local Setup (Without Docker)

### Backend

```bash
cd pmt-backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd pmt-frontend
npm install
ng serve
```

---

## Dockerized Setup

### Prerequisites

- Docker & Docker Compose installed

### Run Everything (Frontend + Backend + DB)

```bash
docker-compose up --build
```

- Frontend: [http://localhost:4200](http://localhost:4200)
- Backend: [http://localhost:8080](http://localhost:8080)
- Swagger UI API Documentation : http://localhost:8080/swagger-ui/index.html
- PostgreSQL: Port 5432

Note: The backend jar is now automatically built inside the Docker image to fix issues of having to run mvn package manually. This is to be sure that a fresh setup works even if you don't yet have local build artifacts. Just be sure to include --build in the command when you do so !

---

## Running Tests

### Backend

```bash
cd pmt-backend
./mvnw test
```

### Frontend

```bash
cd pmt-frontend
npm install
npm run test
```

---

## CI/CD Pipeline

GitHub Actions are used to:

- Build and test backend and frontend
- Build Docker images for both apps

Pipeline runs on `main` branch.
