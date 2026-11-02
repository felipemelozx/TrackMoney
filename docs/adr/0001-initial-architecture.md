# Architectural Decision Record (ADR)

## 0001. Initial Architecture and Technology Decisions

* **Status:** Accepted / Consolidated
* **Date:** 2026-01-11 (Updated: 2026-11-01)
* **Deciders:** Felipe Melo & TrackMoney Team

---

### Context

The TrackMoney project aims to build a comprehensive, production-grade personal finance management platform (Full-Stack), adhering to industry-standard software engineering practices: Clean Architecture, SOLID principles, automated testing, CI/CD, robust security, and observability. The system supports authentication, transaction management, budgets, savings pots, recurring transactions, and reporting dashboards.

---

### Architectural Decisions

1. **Backend Code Organization (Layered Clean Architecture)**
   - Code is structured into well-defined layers under `fun.trackmoney`:
     - `controller/`: REST endpoints, HTTP request handling, input/output DTOs with Bean Validation. Never exposes JPA entities.
     - `service/`: Business rules, transactions (`@Transactional`), and orchestration.
     - `repository/`: Data access via Spring Data JPA and optimized queries/projections.
     - `entity/`: Domain persistence entities.
     - `mapper/`: Compile-time, type-safe DTO <-> Entity mappings using MapStruct.
     - `infra/`: Security configuration (JWT), caching (Caffeine), mail services, and data seeding.
     - `exception/`: Centralized exception handling (`@RestControllerAdvice`).

2. **Core Language and Frameworks**
   - **Java 17 (LTS)** and **Spring Boot 3.4.5**.
   - **Spring Security 6** with stateless dual-token JWT authentication (short-lived Access Token + long-lived Refresh Token).
   - **Spring Data JPA / Hibernate** for relational persistence.

3. **Database and Migrations**
   - **PostgreSQL 16**: Used across both development (via Docker Compose) and production environments, ensuring environment parity and preventing SQL dialect incompatibilities.
   - **Flyway**: Automated database schema migrations and versioning (`src/main/resources/db/migration`).

4. **Frontend**
   - **Angular 19+ (Standalone Components)**: Single Page Application deployed to production.
   - Consumes backend REST APIs secured with JWT, featuring automatic token rotation via functional HTTP interceptors.

5. **Caching and Performance**
   - **Caffeine Cache**: High-performance in-memory cache for frequently accessed read data, minimizing redundant database roundtrips.

6. **Quality and Testing**
   - Code coverage enforcement via **JaCoCo**.
   - Unit testing with **JUnit 5** and **Mockito**.
   - REST endpoint integration testing using **MockMvc**.
   - Continuous static code analysis with **Checkstyle** and **SonarCloud**.

7. **CI/CD and DevOps**
   - **Docker** with multi-stage builds for lightweight, secure container images.
   - **GitHub Actions**: Automated pipeline (Build, Tests, SonarCloud, Docker Build & Push, Deploy).
   - **Spring Boot Actuator**: Health checks and monitoring metrics exposed at `/api/v1/actuator/health`.

---

### Alternatives Considered

| Topic | Evaluated Options | Chosen | Rationale |
|---|---|---|---|
| **Backend Framework** | Spring Boot vs. Quarkus vs. Micronaut | **Spring Boot 3.4** | Ecosystem maturity, comprehensive Spring Security/JPA integration, and corporate enterprise alignment. |
| **Authentication** | Stateful Sessions vs. Dual-Token JWT | **JWT (Access + Refresh)** | Stateless architecture, horizontal scalability support, and fine-grained token lifecycle control. |
| **Local Database** | H2 in-memory vs. PostgreSQL (Docker) | **PostgreSQL (Docker)** | Strict dev/prod parity; eliminates SQL syntax divergences and migration discrepancies. |
| **Frontend** | React vs. Vue vs. Angular | **Angular 19+** | Enterprise-grade structure, strict TypeScript typing, built-in dependency injection, and clean cohesion with Java backends. |
| **Object Mapping** | ModelMapper vs. MapStruct vs. Manual | **MapStruct** | Compile-time code generation with zero reflection overhead, superior performance, and compile-time safety. |

---

### Consequences

#### Positive
- **Domain Isolation:** JPA entities are never leaked through the API layer; contracts are strictly defined by DTOs.
- **Reliability:** Automated CI/CD pipelines guarantee that commits breaking tests or quality gates are caught early.
- **Enhanced Security:** Token rotation and authority-based access control (`USER_ROLES`, `USER_UNVERIFIED`, `REFRESH`, etc.).
- **Maintainability:** Clear separation of concerns simplifies feature expansion and refactoring.

#### Managed Trade-offs
- **Initial Setup Rigor:** Managing dual-token lifecycle and CORS/Security rules requires comprehensive integration testing.
- **Validation Consistency:** Keeping DTOs, mappers, and entities synchronized (mitigated by MapStruct compile-time checks).
