# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Running the Application
```bash
# Start PostgreSQL and Redis (required dependencies)
docker compose up -d

# Run with dev profile (includes Swagger UI)
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Check health endpoint
curl http://localhost:8080/api/v1/actuator/health
```

### Testing
```bash
# Run all tests
./mvnw test

# Build, test with coverage (JaCoCo), and run Checkstyle
./mvnw clean verify

# Run specific test class
./mvnw test -Dtest="AccountServiceTest"

# Run only unit tests (exclude integration tests)
./mvnw test -Dtest="*Test"

# Run only integration tests
./mvnw test -Dtest="*IntegrationTest"
```

### Code Quality
```bash
# Check code style (Checkstyle)
./mvnw checkstyle:check

# View JaCoCo coverage report (after running verify)
# Linux/Mac: open target/site/jacoco/index.html
# Windows: start target/site/jacoco/index.html
```

## Architecture Overview

TrackMoney is a **personal finance management system** built with **Spring Boot 3.4.5** following a **flat layered architecture** where each layer groups all domains together.

### Layer Architecture (Strict Separation)

The codebase organizes code by technical layer (not by domain module):

1. **Controller Layer** (`fun.trackmoney.controller/`)
   - REST endpoints with `@RestController`
   - Input validation using Bean Validation
   - DTOs for request/response (never expose entities directly)
   - HTTP status codes and error handling

2. **Service Layer** (`fun.trackmoney.service/`)
   - Business logic and orchestration
   - DTO ↔ Entity conversion using **MapStruct mappers**
   - Transaction management with `@Transactional`
   - Custom exceptions for domain errors

3. **Repository Layer** (`fun.trackmoney.repository/`)
   - Spring Data JPA repositories
   - Custom queries using `@Query` or query methods
   - Projection interfaces in `repository/projection/`
   - Database abstraction

4. **Entity Layer** (`fun.trackmoney.entity/`)
   - JPA entities mapped to PostgreSQL tables
   - **All primary keys use UUID** (`@Id` with `@GeneratedValue(UUID)`) — except `CategoryEntity` which uses `IDENTITY`
   - Relationships between domain objects

### Package Structure

```
fun.trackmoney/
├── controller/          # All REST controllers (one file per domain)
├── service/             # All business services (one file per domain)
├── entity/              # All JPA entities
├── repository/          # All Spring Data JPA repositories
│   └── projection/      # Repository projection interfaces
├── mapper/              # All MapStruct mapper interfaces
├── dto/                 # DTOs organized by domain sub-package
│   ├── account/
│   ├── auth/
│   │   └── internal/   # Auth result types (login, register, forgot-password)
│   ├── budget/
│   │   └── internal/
│   ├── category/
│   ├── metrics/
│   │   └── response/
│   ├── pots/
│   │   └── internal/
│   ├── recurring/
│   ├── transaction/
│   │   └── internal/
│   └── user/
├── config/              # Application configuration
│   ├── exception/       # Global exception handler
│   └── swagger/         # Swagger/OpenAPI config
├── infra/               # Infrastructure concerns
│   ├── auth/config/     # Security config & JWT filter
│   ├── email/           # Email service
│   ├── jwt/             # JWT token service
│   └── redis/           # Redis cache config & manager
├── exception/           # Domain-specific exceptions
├── enums/               # Shared and domain-specific enums
├── seed/                # Seed data generation (dev only)
│   ├── config/          # Startup runners
│   ├── service/
│   │   ├── generator/   # Entity generators
│   │   └── model/       # Seed data models
│   └── util/            # Random utilities
└── utils/               # Shared utilities
    └── response/        # ApiResponse wrapper
```

**Important**: Always use DTOs for API operations. Never expose entities directly to controllers.

### Key Architectural Patterns

1. **DTO Strategy**: All API operations use DTOs to decouple domain model from API contracts
2. **MapStruct**: Compile-time mapping for performance (type-safe, no reflection overhead)
3. **Custom Exceptions**: Domain-specific exceptions (not generic RuntimeException)
4. **JWT Dual-Token**: Access token (15 min) + Refresh token (7 days) for security
5. **Flyway Migrations**: Versioned database schema in `src/main/resources/db/migration/`

## Security Architecture

### JWT Authentication (Dual-Token Strategy)
- **Access Token**: Short-lived (15 minutes), used in all authenticated requests
- **Refresh Token**: Long-lived (7 days), used only to obtain new access tokens
- Tokens are generated/validated in `infra/jwt/JwtService.java`

### Authorities (Granular Permissions)
Not just roles, but fine-grained authorities:
- `USER_ROLES` - Authenticated and verified users (most endpoints)
- `USER_UNVERIFIED` - Registered but email not verified (limited endpoints)
- `RESET_PASSWORD` - Temporary token for password reset flow
- `REFRESH` - Token refresh endpoint only

### Security Configuration
- `SecurityConfig.java` defines endpoint protection rules
- **Swagger is blocked in production** (only available in dev profile)
- **CORS restricted** to authorized frontend domain only
- All password hashing uses BCrypt

## Database

### PostgreSQL with Flyway
- Migrations located in `src/main/resources/db/migration/`
- Naming convention: `V{number}__description.sql`
- All tables use `tb_` prefix (e.g., `tb_user`, `tb_transaction`)
- All primary keys are UUID type

### Key Tables
- `tb_user` - User accounts with email verification
- `tb_account` - Financial accounts (bank, wallet, etc.)
- `tb_transaction` - Income and expenses
- `tb_category` - Transaction categories
- `tb_budget` - Monthly budget limits per category
- `tb_pots` - Savings pots for specific goals
- `tb_recurring` - Recurring transaction rules

## Cache Strategy

### Redis Cache
- Used for frequently accessed data (categories, user settings)
- Spring Cache abstraction with Redis backend
- Application-level caching enabled via `@EnableCaching`
- Cache configuration in `infra/redis/` package

## Testing Strategy

### Test Structure
- Unit tests: `*Test.java` (use Mockito for dependencies)
- Integration tests: `*IntegrationTest.java` or `*ControllerTest.java` (use `@SpringBootTest` with MockMvc)
- Test utilities in `src/test/java/fun/trackmoney/testutils/` (Factories and Builders)
- **Coverage requirement**: >70% (enforced by JaCoCo)
- **Entities and Configs excluded** from coverage requirements

### Test Data Builders
Use the Factory/Builder classes in `testutils/` for creating test data:
```java
AccountEntity account = AccountEntityFactory.defaultAccount();
TransactionEntity transaction = TransactionEntityFactory.defaultExpenseNow();
```

## Important Constraints

### Do Not Do
- **Never expose entities directly to controllers** - always use DTOs
- **Never add validation logic to controllers** - it belongs in services or DTOs
- **Never use reflection for mapping** - use MapStruct mappers
- **Never add Swagger without checking profile** - blocked in production
- **Never commit without running tests** - CI will fail anyway

### Do
- **Always create DTOs** for new endpoints
- **Always create MapStruct mappers** for DTO ↔ Entity conversion
- **Always add validation** to request DTOs (`@NotNull`, `@NotBlank`, etc.)
- **Always write tests** for new services and controllers
- **Always create migrations** for schema changes

## Configuration

### Application Configuration
- Main config: `src/main/resources/application.yaml`
- Dev profile: `src/main/resources/application-dev.yaml`
- Prod profile: `src/main/resources/application-prod.yaml`
- Environment variables required: `API_SECRET_KEY`, `POSTGRES_*`, `SPRING_MAIL_*`

### Context Path
- API base path: `/api/v1` (configurable via `SERVER_CONTEXT_PATH`)
- All endpoints are prefixed with this path

## Development Workflow

1. **Feature development**: Create feature branch from `main`
2. **Local testing**: Run `./mvnw clean verify` before committing
3. **Commit**: Use conventional commits (`feat:`, `fix:`, `refactor:`, etc.)
4. **PR**: Open pull request to `main` (CI runs automatically)
5. **Deploy**: Merging to `main` triggers automatic deployment to production

## Current Context

- **Active branch**: `feat/budget` (budget-related features in development)
- **Recent changes**: Budget service updates, report entity removal, new dashboard/metrics features
- **Production URL**: https://trackmoney.fun
- **API Base URL**: https://api.trackmoney.fun/api/v1