# Financial Management Application (Backend)

## Overview

This is the backend part of a full-stack financial management application, designed to handle essential features like user authentication, CRUD operations for financial resources (transactions, budgets, recurring accounts), and more. The backend is built using **Java 17**, **Spring Boot**, **Spring Security**, and follows the **Clean Architecture** principles. It is integrated with a PostgreSQL database and includes a CI/CD pipeline for continuous integration and deployment.

## Technologies Used

### Backend Framework

- **Java 17** with **Spring Boot 3.4.4**
    - A powerful, flexible framework for building REST APIs and backend services.

### Authentication & Security

- **Spring Security** with **JWT** (JSON Web Token)
    - Stateless authentication for secure user login and access control (USER, ADMIN roles).
    - Protection against common vulnerabilities like **SQL Injection** and **CORS** issues.

### Database

- **H2 Database** for development
- **PostgreSQL** for production
    - **Flyway** for database migrations, ensuring a smooth versioned database management process.

### Testing

- **JUnit 5** for unit and integration tests.
- **Mockito** for mocking dependencies during tests.
- **Cypress** for end-to-end testing (E2E) on the frontend (integrated via APIs).

### CI/CD

- **GitHub Actions** for Continuous Integration and Deployment.
    - Pipelines include linting, unit tests, static analysis (CodeQL, SonarQube), and automatic deployment to AWS EC2.

### Docker

- **Docker** for containerizing the backend application.
    - **Docker Compose** is used for multi-container applications (backend, database, and cache services).

### Infrastructure Management

- **Terraform** for automating infrastructure provisioning on **AWS**.
    - The infrastructure includes resources like **EC2**, **RDS** (for PostgreSQL), and **VPC**.

## Key Features

- **User Authentication:**
    - JWT-based authentication with login, registration, and profile endpoints (`/auth`).
    - Role-based access control (USER, ADMIN).
- **CRUD Operations for Financial Resources:**
    - **Transactions:** Create, read, update, and delete financial transactions.
    - **Budget:** Manage budget categories, limits, and status.
    - **Recurring Accounts:** Set up and manage recurring financial transactions.
- **Database Layer:**
    - Entity models representing the core financial concepts.
    - Repository layer interacting with the PostgreSQL database using **JPA**.
- **API Layer:**
    - RESTful APIs exposing endpoints for each financial entity.
    - DTOs (Data Transfer Objects) used to transfer data efficiently between client and server.
- **Service Layer:**
    - Contains the core business logic, orchestrating the interactions between the controller and the repository layers.

## Architecture Overview

The backend follows a **Clean Architecture** approach, organizing the code into layers that ensure separation of concerns and high maintainability:

1. **Controller Layer**: Handles HTTP requests, invoking the corresponding service methods.
2. **Service Layer**: Contains the business logic and interacts with the repository.
3. **Repository Layer**: Interacts with the database using JPA and manages CRUD operations.
4. **DTO Layer**: Defines the data models for API input and output, ensuring loose coupling between the layers.

## Setup & Development

### Prerequisites

- **Java 17** (or later)
- **Maven** (for dependency management and building the project)
- **Docker** (for containerizing the application)
- **PostgreSQL** (local or cloud instance for production)

### Running the Project Locally

1. Clone the repository:

```bash
bash
CopyEdit
git clone https://github.com/your-username/financial-management-backend.git

```

1. Navigate into the project folder:

```bash
bash
CopyEdit
cd financial-management-backend

```

1. Set up your PostgreSQL instance (either locally or using Docker).
    - If using Docker, you can set up the database using a `docker-compose.yml` file.
2. Build and run the application:

```bash
bash
CopyEdit
mvn spring-boot:run

```

1. The backend will be running on `http://localhost:8080`.

### Docker Setup

For development, you can use Docker Compose to spin up the application with its dependencies (database, cache, etc.):

1. Build the Docker image:

```bash
bash
CopyEdit
docker-compose build

```

1. Run the application:

```bash
bash
CopyEdit
docker-compose up

```

This will start the application along with PostgreSQL and other services defined in `docker-compose.yml`.

## CI/CD Pipeline

The project uses **GitHub Actions** for CI/CD to ensure that code changes are tested, built, and deployed automatically:

1. **Linting**: Automatically checks code style and quality.
2. **Unit Tests**: Runs unit tests on each push to ensure code correctness.
3. **Static Code Analysis**: Using **SonarQube** and **CodeQL** for security and code quality scanning.
4. **Deployment**: Automatically deploys the application to **AWS EC2** once the code is merged into the `main` branch.

## Future Improvements

- **PDF Report Generation**: Allow users to download financial summaries and transaction histories as PDF reports.
- **External Integrations**: Integrate with third-party services such as **Google Sheets** or **external financial APIs**.
- **Redis Caching**: Implement caching for performance improvements, especially for frequently accessed data.

## License

This project is licensed under the MIT License - see the LICENSE file for details.