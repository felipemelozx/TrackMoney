# TrackMoney — Project Documentation

Welcome to the technical and engineering documentation for **TrackMoney**. This directory consolidates architecture guidelines, endpoint specifications, decision records, and standards across the ecosystem.

---

## Documentation Structure

```
docs/
├── README.md                           # General documentation index (this file)
├── adr/                                # Architectural Decision Records (ADRs)
│   └── 0001-initial-architecture.md   # Initial Architecture, Stack, and Engineering Decisions
└── api/                                # Detailed endpoint contracts and specifications
    └── delete-account-spec.md          # Account Deletion Specification (Hard Delete)
```

---

## Core Sections

### 1. [Architecture & Technical Decisions (ADRs)](adr/0001-initial-architecture.md)
- Stack decisions (Java 17, Spring Boot 3.4.5, PostgreSQL 16, Flyway).
- Application layers and responsibilities (`controller`, `service`, `repository`, `entity`, `dto`, `infra`).
- Dual-Token JWT authentication strategy (Access + Refresh Token).

### 2. [API Specifications](api/delete-account-spec.md)
- REST contracts, request/response payloads, and HTTP status codes.
- [Permanent Account Deletion (`DELETE /api/v1/user`)](api/delete-account-spec.md).

---

## Useful Links

- **Backend Repository**: [felipemelozx/Trackmoney](https://github.com/felipemelozx/Trackmoney)
- **Production Application**: [https://trackmoney.felipemelozx.com](https://trackmoney.felipemelozx.com)
- **Main README Guide**: [README.md](../README.md)
