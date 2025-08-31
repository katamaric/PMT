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

## Dockerized Setup

### Prerequisites

- Docker & Docker Compose installed

### IMPORTANT FIRST STEP OR ELSE BAD ERRORS FOR NOT HAVING MAIL SERVICE CREDENTIALS

You must create a .env file in the root PMT folder to input the variables for :

```bash
SPRING_MAIL_USERNAME=your_username
SPRING_MAIL_PASSWORD=your_password
```

or else task creation and updates will fail due to its link to MailTrapIO for the project requirements of email notifications. So please do not forget to do this first !

### Run Everything (Frontend + Backend + DB)

```bash
docker-compose down -v
docker-compose pull
docker-compose up -d
```

- Frontend: [http://localhost:4200](http://localhost:4200)
- Swagger UI API Documentation : http://localhost:8080/swagger-ui/index.html

### To ensure latest changes are displayed, please don't forget to give it a few moments to ensure the back is up for proper error handling with docker and do this to refresh the front :

- Ctrl + Shift + R / Cmd + Shift + R to force refresh if using Chrome
- Or to use a private window if using Safari to have a clean cache

---

## Test Coverage Reports

### Backend

Using Eclipse, you can run the PMTBackendApplication.java project as a Maven build, being sure to input "clean test jacoco:report" for Goals.

The report summary will then be generated and can be opened through the terminal in VSCode from the root PMT project folder via :

```bash
cd pmt-backend
open target/site/jacoco/index.html
```

### Frontend

From the root PMT project folder in VSCode, you can use :

```bash
cd pmt-frontend
ng test --code-coverage
```

The tests should then run in a pop-up browser and you can view the coverage summary directly in the VSCode terminal.

---

## Else, Running Tests Normally w/o Coverage

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
- Push images to Docker Hub
- Run pipeline on `main` branch
