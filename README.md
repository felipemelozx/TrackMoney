# TrackMoney 💰

**Sistema completo de gerenciamento financeiro pessoal** desenvolvido com arquitetura robusta, segurança avançada e práticas profissionais de desenvolvimento. Projeto production-ready demonstrando expertise em desenvolvimento full-stack, DevOps e engenharia de software.

---

## 🚀 Demonstração ao Vivo

> **Aplicação em Produção**: [https://trackmoney.fun](https://trackmoney.fun)

A aplicação completa está disponível para testes em ambiente de produção. Crie sua conta e explore todas as funcionalidades do sistema de gestão financeira.

### 🔗 Endpoints

- **Frontend**: [https://trackmoney.fun](https://trackmoney.fun)
- **API REST**: `https://api.trackmoney.fun/api/v1`
- **Health Check**: [https://api.trackmoney.fun/api/v1/health](https://api.trackmoney.fun/api/v1/health)

> **Nota sobre Swagger**: A documentação Swagger está disponível apenas em ambiente de desenvolvimento local por questões de segurança. Em produção, o acesso é restrito via Spring Security configurado em [`SecurityConfig.java`](/src/main/java/fun/trackmoney/auth/infra/config/SecurityConfig.java#L45-L49).

---

## 💡 Sobre o Projeto

TrackMoney é mais do que uma simples aplicação de controle financeiro - é um **projeto profissional completo** que demonstra:

### 🎯 Diferenciais Técnicos

- **Arquitetura Enterprise**: Clean Architecture com separação clara de responsabilidades
- **Segurança Robusta**: Implementação completa de OAuth2/JWT com refresh tokens
- **Qualidade de Código**: Cobertura de testes, análise estática (SonarCloud) e Checkstyle
- **CI/CD Profissional**: Pipeline automatizado com múltiplos stages de validação
- **Production-Ready**: Deploy em produção com Docker, monitoramento e health checks
- **Boas Práticas**: SOLID, DRY, padrões de design e código limpo

### 🏆 Complexidade e Skills Demonstradas

| Área | Tecnologias & Práticas |
|------|----------------------|
| **Backend** | Java 17, Spring Boot 3.4, Spring Security 6, JPA/Hibernate |
| **Arquitetura** | Layered Architecture, DTOs, MapStruct, Exception Handling |
| **Segurança** | JWT, BCrypt, CORS configurado, proteção CSRF, validação de entrada |
| **Persistência** | PostgreSQL, Redis Cache, Flyway Migrations, transações |
| **Testes** | JUnit 5, Mockito, testes unitários e de integração, JaCoCo |
| **DevOps** | Docker, Docker Compose, GitHub Actions, self-hosted runner |
| **Qualidade** | SonarCloud, Checkstyle, cobertura de código, análise estática |
| **Monitoramento** | Spring Actuator, health checks, logs estruturados |

---

## ✨ Funcionalidades Implementadas

### Gestão Financeira Completa
- [x] **Transações**: Sistema completo de receitas e despesas com filtros avançados
- [x] **Orçamentos**: Defina limites mensais e acompanhe gastos por categoria
- [x] **Metas Financeiras**: Estabeleça objetivos e visualize progresso em tempo real
- [x] **Potes (Savings Pots)**: Separe dinheiro para objetivos específicos
- [x] **Transações Recorrentes**: Configure pagamentos e recebimentos automáticos

### Segurança e Autenticação
- [x] **JWT Authentication**: Access token (curta duração) + Refresh token (longa duração)
- [x] **Controle de Acesso Granular**: Authorities personalizadas (USER_ROLES, USER_UNVERIFIED, RESET_PASSWORD, REFRESH)
- [x] **Recuperação de Senha**: Fluxo completo via e-mail com tokens temporários
- [x] **Verificação de E-mail**: Confirmação obrigatória de conta via link
- [x] **Password Hashing**: BCrypt com salt automático
- [x] **Proteção de Rotas**: Spring Security configurado para cada endpoint
- [x] **CORS Restrito**: Apenas frontend autorizado pode consumir a API

### Infraestrutura e Qualidade
- [x] **Cache Redis**: Otimização de performance para dados frequentes
- [x] **Envio de E-mails**: Templates Thymeleaf para notificações profissionais
- [x] **Migrations Versionadas**: Flyway para controle de schema
- [x] **Health Checks**: Endpoints para monitoramento de disponibilidade
- [x] **Validação Robusta**: Bean Validation em todas as camadas
- [ ] **Logging Estruturado**: Rastreamento de operações e erros

---

## 🏗️ Arquitetura e Decisões Técnicas

### Arquitetura em Camadas (Layered Architecture)

O projeto segue rigorosamente a **separação de responsabilidades** através de camadas bem definidas:

```
┌─────────────────────────────────────────┐
│         Controller Layer                │  ← Apresentação (HTTP/REST)
│  • REST Controllers                     │
│  • DTOs de Request/Response             │
│  • Validação de entrada                 │
│  • Documentação OpenAPI                 │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Service Layer                  │  ← Lógica de Negócio
│  • Regras de negócio                    │
│  • Orquestração de operações            │
│  • Conversão DTO ↔ Entity               │
│  • Validações complexas                 │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        Repository Layer                 │  ← Persistência
│  • Spring Data JPA                      │
│  • Queries customizadas                 │
│  • Transações                           │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         Database (PostgreSQL)           │  ← Armazenamento
└─────────────────────────────────────────┘
```

### Decisões Arquiteturais e Justificativas

#### 1. **DTOs vs Entities Diretamente**
**Decisão**: Utilizar DTOs em todas as operações de entrada/saída da API.

**Justificativa**:
- Desacoplamento entre modelo de domínio e contratos de API
- Controle fino sobre dados expostos (evita vazamento de informações sensíveis)
- Facilita versionamento da API sem impactar banco de dados
- Permite validações específicas por caso de uso

#### 2. **MapStruct para Mapeamentos**
**Decisão**: Usar MapStruct ao invés de mapeamento manual ou reflection.

**Justificativa**:
- Geração de código em compile-time (sem overhead de runtime)
- Type-safe (erros detectados na compilação)
- Performance superior a bibliotecas baseadas em reflection
- Código gerado é debugável e inspecionável

#### 3. **JWT com Access + Refresh Tokens**
**Decisão**: Implementar estratégia dual-token ao invés de sessões.

**Justificativa**:
- Stateless: permite escalabilidade horizontal sem sticky sessions
- Access token curto: reduz janela de exposição em caso de vazamento
- Refresh token longo: melhor UX (usuário não precisa fazer login frequentemente)
- Possibilidade de revogação via Redis (refresh tokens podem ser blacklistados)

#### 4. **Authorities Granulares**
**Decisão**: Usar authorities específicas (USER_ROLES, USER_UNVERIFIED, etc.) ao invés de apenas roles.

**Justificativa**:
- Controle fino de acesso a recursos específicos
- Suporta fluxos complexos (verificação de e-mail, reset de senha)
- Facilita lógica condicional na aplicação
- Separação clara entre estados de usuário

#### 5. **Flyway para Migrations**
**Decisão**: Versionamento de banco via Flyway ao invés de scripts manuais.

**Justificativa**:
- Histórico completo de mudanças no schema
- Deploy reproduzível em qualquer ambiente
- Rollback facilitado em caso de problemas
- Integração com CI/CD para validação automática

#### 6. **Redis para Cache**
**Decisão**: Implementar camada de cache com Redis.

**Justificativa**:
- Redução significativa de carga no PostgreSQL
- Latência ultra-baixa para dados frequentes (categorias, configurações)

## 🛠️ Stack Tecnológica

### Backend Framework

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.5</version>
</dependency>
```

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Java** | 17 (LTS) | Linguagem principal com features modernas |
| **Spring Boot** | 3.4.5 | Framework enterprise para desenvolvimento rápido |
| **Spring Security** | 6.x | Autenticação, autorização e proteções |
| **Spring Data JPA** | 3.x | Abstração de persistência ORM |
| **Spring Validation** | 3.x | Validação declarativa de beans |
| **Spring Mail** | 3.x | Envio de e-mails transacionais |
| **Spring Actuator** | 3.x | Monitoramento e health checks |

### Segurança e Autenticação

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Auth0 Java JWT** | 4.4.0 | Geração e validação de tokens JWT |
| **BCrypt** | (Spring Security) | Hashing seguro de senhas |
| **CORS** | (Spring Security) | Controle de origem de requisições |

### Persistência e Cache

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **PostgreSQL** | 15+ | Banco de dados relacional ACID |
| **Redis** | 7+ | Cache em memória e sessões |
| **Flyway** | 9.8.1 | Migrations e versionamento de schema |


### Mapeamento e Conversões

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **MapStruct** | 1.5.5 | Mapeamento DTO ↔ Entity performático |


### Documentação

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **SpringDoc OpenAPI** | 2.8.6 | Geração automática de documentação Swagger |
| **Swagger UI** | (via SpringDoc) | Interface interativa de documentação |

### Testes e Qualidade

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **JUnit 5** | (Spring Boot) | Framework de testes unitários |
| **Mockito** | (Spring Boot) | Mocking para testes isolados |
| **Spring Boot Test** | 3.4.5 | TestContainers e testes de integração |
| **H2 Database** | (test scope) | Banco em memória para testes |
| **JaCoCo** | 0.8.10 | Relatórios de cobertura de código |
| **Checkstyle** | 10.14.1 | Análise de estilo e padrões |
| **SonarCloud** | - | Análise estática de qualidade e segurança |

### DevOps e Build

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| **Maven** | 3.8+ | Gerenciamento de dependências e build |
| **Docker** | 24+ | Containerização da aplicação |
| **Docker Compose** | 2.x | Orquestração multi-container |
| **GitHub Actions** | - | CI/CD automatizado |

---

## 📚 Documentação da API

### Ambientes

| Ambiente | Base URL | Swagger | Acesso |
|----------|----------|---------|--------|
| **Produção** | `https://api.trackmoney.fun/api/v1` | ❌ Bloqueado (segurança) | Via frontend |
| **Desenvolvimento** | `http://localhost:8080/api/v1` | ✅ Disponível | Local |

### Acessando o Swagger (Desenvolvimento Local)

O Swagger UI está disponível **apenas em ambiente de desenvolvimento** para segurança:

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

Em **produção**, o acesso ao Swagger é explicitamente negado via Spring Security:

```java
// SecurityConfig.java - Linhas 45-49
.requestMatchers(
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**"
).denyAll()
```

**Motivo**: Prevenir exposição de contratos internos da API e reduzir superfície de ataque.

### Padrão REST

A API segue rigorosamente os princípios **RESTful**:

- ✅ Métodos HTTP semânticos (GET, POST, PUT, DELETE)
- ✅ Recursos representados por substantivos (`/transactions`, `/budgets`)
- ✅ Códigos de status HTTP apropriados
- ✅ Versionamento via path (`/api/v1`)
- ✅ Respostas em JSON padronizado
- ✅ Stateless (sem sessões no servidor)

### HTTP Status Codes

| Status | Significado | Quando Ocorre |
|--------|-------------|---------------|
| `200 OK` | Sucesso | GET, PUT bem-sucedidos |
| `201 Created` | Recurso criado | POST bem-sucedido |
| `204 No Content` | Sucesso sem retorno | DELETE bem-sucedido |
| `400 Bad Request` | Dados inválidos | Validação falhou |
| `401 Unauthorized` | Não autenticado | Token ausente ou inválido |
| `403 Forbidden` | Sem permissão | Authority insuficiente |
| `404 Not Found` | Recurso inexistente | ID não encontrado |
| `500 Internal Server Error` | Erro do servidor | Exceção não tratada |

### Autenticação JWT (Dual-Token Strategy)

A API implementa **estratégia de segurança dual-token** com Access Token de curta duração e Refresh Token de longa duração.

#### Arquitetura de Tokens

```
┌─────────────────┐
│  Access Token   │  ← Usado em TODAS as requisições autenticadas
│  (15 minutos)   │  ← Contém: userId, email, authorities
│                 │  ← Stateless (verificado via signature)
└─────────────────┘

┌─────────────────┐
│  Refresh Token  │  ← Usado APENAS para renovar access token
│  (7 dias)       │  ← Armazenado em HttpOnly cookie (frontend)
│                 │  ← Pode ser revogado via Redis
└─────────────────┘
```

#### Fluxo de Autenticação

1. **Login**: `POST /api/v1/auth/login`
   ```http
   POST /api/v1/auth/login HTTP/1.1
   Host: api.trackmoney.fun
   Content-Type: application/json

   {
     "email": "user@example.com",
     "password": "senha123"
   }
   ```

   **Resposta**:
   ```json
   {
     "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```

2. **Requisições Autenticadas**: Incluir `Authorization` header
   ```http
   GET /api/v1/transactions HTTP/1.1
   Host: api.trackmoney.fun
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

3. **Renovação de Token**: `GET /api/v1/auth/refresh`
   ```http
   GET /api/v1/auth/refresh HTTP/1.1
   Host: api.trackmoney.fun
   Authorization: Bearer <refresh_token>
   ```

   **Resposta**:
   ```json
   {
     "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
   }
   ```

#### Authorities e Controle de Acesso

| Authority | Descrição | Endpoints com Acesso |
|-----------|-----------|----------------------|
| `USER_ROLES` | Usuário autenticado e verificado | Maioria dos endpoints |
| `USER_UNVERIFIED` | Usuário cadastrado mas e-mail não verificado | `/auth/verify-email`, `/auth/resend-verification` |
| `RESET_PASSWORD` | Token temporário de reset de senha | `/auth/reset-password` |
| `REFRESH` | Token de renovação | `/auth/refresh` |

### CORS Configuration

A API possui **CORS restrito** apenas ao frontend autorizado:

```java
// CorsConfig.java
config.setAllowedOrigins(List.of("https://trackmoney.fun"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowCredentials(true);
```

**Para testar localmente**: Configure o frontend para rodar em `http://localhost:4200` e ajuste o `.env`:
```env
FRONT_URL=http://localhost:4200
```

---

## ▶️ Executando Localmente

### Pré-requisitos

| Software | Versão Mínima | Download |
|----------|---------------|----------|
| **Java JDK** | 17 (LTS) | [Adoptium](https://adoptium.net/) |
| **Maven** | 3.8+ | [Maven](https://maven.apache.org/) (ou use o wrapper) |
| **Docker** | 24+ | [Docker Desktop](https://www.docker.com/products/docker-desktop/) |
| **Git** | 2.x | [Git SCM](https://git-scm.com/) |

### Setup Rápido

#### 1. Clone o Repositório

```bash
git clone https://github.com/felipemelozx/TrackMoney.git
cd TrackMoney
```

#### 2. Configure Variáveis de Ambiente

Copie o arquivo de exemplo e personalize:

```bash
cp .env.example .env
```

Edite `.env` com suas configurações:

```env
# Database
POSTGRES_USER=trackmoney_user
POSTGRES_PASSWORD=SuaSenhaSegura123!
POSTGRES_DB=trackmoney_db

# Security - IMPORTANTE: Gere uma chave forte!
API_SECRET_KEY=sua_chave_jwt_base64_muito_longa

# Email (opcional para dev, necessário para recuperação de senha)
SPRING_MAIL_USERNAME=seuemail@gmail.com
SPRING_MAIL_PASSWORD=sua-senha-de-app

# Frontend
FRONT_URL=http://localhost:4200
```

> 💡 **Gerar chave JWT segura**:
> ```bash
> openssl rand -base64 64
> ```

#### 3. Suba PostgreSQL e Redis

```bash
docker compose -f docker/docker-compose.yaml up -d
```

Aguarde os containers iniciarem:
```bash
docker compose -f docker/docker-compose.yaml ps
```

Deve mostrar `postgres` e `redis` como `running`.

#### 4. Execute a Aplicação

**Maven Wrapper (recomendado)**:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

**Maven instalado**:
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

Aguarde a mensagem:
```
Started TrackMoneyApplication in X.XXX seconds
```

#### 5. Verifique o Health Check

```bash
curl http://localhost:8080/api/v1/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

#### 6. Acesse o Swagger

Abra no navegador:
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

## 🧪 Testes e Qualidade de Código

O projeto possui **cobertura abrangente de testes** e **processos rigorosos de qualidade** para garantir confiabilidade production-ready.

### Estratégia de Testes

#### Testes Unitários (Unit Tests)

- **Escopo**: Testam métodos e classes isoladamente
- **Características**:
  - Usam **Mockito** para mockar dependências externas
  - Focam em lógica de negócio pura (services, utils)
  - Executam rapidamente (sem I/O)
  - Seguem padrão **AAA** (Arrange, Act, Assert)

**Exemplo**:
```java
@Test
void shouldCalculateTotalExpensesCorrectly() {
    // Arrange
    List<Transaction> transactions = List.of(
        new Transaction(100.0, EXPENSE),
        new Transaction(50.0, EXPENSE)
    );
    when(repository.findAll()).thenReturn(transactions);
    
    // Act
    Double total = service.calculateTotalExpenses();
    
    // Assert
    assertEquals(150.0, total);
}
```

#### Testes de Integração (Integration Tests)

- **Escopo**: Testam integração entre camadas (Controller → Service → Repository → Database)
- **Características**:
  - Usam **H2 in-memory database** para isolamento
  - Testam endpoints REST com **MockMvc**
  - Validam serialização JSON e validações
  - Testam transações e rollbacks

**Exemplo**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {
    
    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 100.0, \"type\": \"EXPENSE\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
```

### Ferramentas de Qualidade

| Ferramenta | Propósito | Threshold |
|------------|-----------|-----------|
| **JaCoCo** | Cobertura de código | > 70% de cobertura |
| **Checkstyle** | Padrões de código | 0 violações críticas |
| **SonarCloud** | Qualidade e segurança | Grade A |
| **Spring Security Test** | Testes de autenticação | - |

### Executando Testes

```bash
# Todos os testes
./mvnw test

# Testes com relatório de cobertura
./mvnw clean verify

# Apenas testes unitários
./mvnw test -Dtest="*Test"

# Apenas testes de integração
./mvnw test -Dtest="*IntegrationTest"
```

### Relatórios de Cobertura

Após executar `mvn verify`, abra o relatório JaCoCo:

```bash
# Linux/Mac
open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Análise de Código

```bash
# Checkstyle (padrões de código)
./mvnw checkstyle:check

# Ver relatório Checkstyle
cat target/checkstyle-result.xml

# SonarCloud (requer configuração)
./mvnw sonar:sonar \
  -Dsonar.projectKey=felipemelozx_TrackMoney \
  -Dsonar.organization=felipemelozx-trackmoney \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=$SONAR_TOKEN
```

### Métricas de Qualidade

| Métrica | Status Atual | Objetivo |
|---------|--------------|----------|
| **Cobertura de Código** | 70%+ | > 80% |
| **Code Smells** | A | Grade A |
| **Vulnerabilidades** | 0 | 0 |
| **Bugs** | 0 | 0 |
| **Duplicação** | < 3% | < 5% |

---

## ⚙️ CI/CD Pipeline

Pipeline **totalmente automatizado** com GitHub Actions, executando testes, análises e deploy em cada push para `main`.

### Visão Geral do Pipeline

```mermaid
graph LR
    A[Push to main] --> B[Build & Test]
    B --> C[Sonar Analysis]
    C --> D[Build Docker Image]
    D --> E[Push to Docker Hub]
    E --> F[Deploy to VPS]
    F --> G[Health Check]
```

### Stages do Pipeline

#### 1️⃣ Build & Test (5-7 min)

```yaml
- Checkout código
- Setup Java 17
- Cache Maven dependencies
- Docker Compose up (PostgreSQL + Redis)
- Run tests (unit + integration)
- Generate coverage reports (JaCoCo, Checkstyle)
- Upload artifacts
```

**Critério de Sucesso**: ✅ Todos os testes passam

#### 2️⃣ Sonar Analysis (3-5 min)

```yaml
- Download coverage reports
- Run SonarCloud analysis
- Check quality gates
```

**Critério de Sucesso**: ✅ Quality Gate PASSED (Grade A, 0 bugs, 0 vulnerabilities)

#### 3️⃣ Build & Push Docker Image (4-6 min)

```yaml
- Extract version from pom.xml
- Build JAR (maven package)
- Build Docker image
- Tag with version + latest
- Push to Docker Hub
- Cache Docker layers
```

**Output**: 
- `felipemelozx/trackmoney:latest`
- `felipemelozx/trackmoney:0.0.2` (exemplo)

#### 4️⃣ Deploy to VPS (2-3 min)

```yaml
- Run on self-hosted runner
- Pull latest image
- Generate .env from GitHub Secrets
- Docker Compose down
- Docker Compose up with new image
- Prune old images
```

**Critério de Sucesso**: ✅ Container healthy

### Ambientes

| Ambiente | Branch | Deploy | URL | Status |
|----------|--------|--------|-----|--------|
| **Produção** | `main` | ✅ Automático | https://trackmoney.fun | [![Deploy](https://github.com/felipemelozx/TrackMoney/workflows/TrackMoney%20Pipeline/badge.svg)](https://github.com/felipemelozx/TrackMoney/actions) |
| **Desenvolvimento** | `develop`, `feature/*` | ❌ Manual | http://localhost:8080 | - |

### GitHub Secrets Necessários

Para replicar o pipeline, configure os seguintes secrets (Settings → Secrets → Actions):

#### Docker Hub
```
DOCKER_USERNAME=seu-usuario-dockerhub
DOCKER_PASSWORD=seu-token-dockerhub
```

#### Database
```
POSTGRES_USER=trackmoney_user
POSTGRES_PASSWORD=senha-muito-segura
POSTGRES_DB=trackmoney_db
```

#### Email (envio de notificações)
```
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=seu-email@gmail.com
SPRING_MAIL_PASSWORD=senha-de-app-gmail
```

#### Security
```
API_SECRET_KEY=<resultado de: openssl rand -base64 64>
```

#### Application
```
FRONT_URL=https://trackmoney.fun
```

#### Análise de Código
```
SONAR_TOKEN=seu-token-sonarcloud
```

### Monitoramento do Pipeline

- **GitHub Actions**: [Ver execuções](https://github.com/felipemelozx/TrackMoney/actions)
- **SonarCloud**: [Dashboard de qualidade](https://sonarcloud.io/project/overview?id=felipemelozx_TrackMoney)
- **Docker Hub**: [Imagens publicadas](https://hub.docker.com/r/felipemelozx/trackmoney)

### Fluxo de Trabalho Recomendado

```bash
# 1. Crie uma branch de feature
git checkout -b feature/nova-funcionalidade

# 2. Desenvolva e teste localmente
./mvnw test

# 3. Commit e push
git add .
git commit -m "feat: adiciona nova funcionalidade"
git push origin feature/nova-funcionalidade

# 4. Abra Pull Request para main
# (testes rodam automaticamente no PR)

# 5. Após aprovação e merge para main
# → Pipeline de deploy roda automaticamente
# → Aplicação é atualizada em produção
```

---

## 🤝 Contribuindo

Contribuições são **muito bem-vindas**! Este projeto está aberto para melhorias, correções de bugs e novas funcionalidades.

### Como Contribuir

#### 1. Fork e Clone

```bash
# Fork no GitHub e depois:
git clone https://github.com/SEU-USUARIO/TrackMoney.git
cd TrackMoney
git remote add upstream https://github.com/felipemelozx/TrackMoney.git
```

#### 2. Crie uma Branch

```bash
git checkout -b feature/minha-feature
# ou
git checkout -b fix/correcao-bug
```

#### 3. Desenvolva

- Escreva código limpo seguindo os padrões do Checkstyle
- Adicione testes para novas funcionalidades
- Mantenha cobertura de código acima de 70%
- Atualize documentação se necessário

#### 4. Teste Localmente

```bash
# Rode todos os testes
./mvnw clean verify

# Verifique o Checkstyle
./mvnw checkstyle:check
```

#### 5. Commit

Siga o padrão [Conventional Commits](https://www.conventionalcommits.org/):

```bash
git commit -m "feat: adiciona filtro por período em transações"
git commit -m "fix: corrige cálculo de saldo em contas"
git commit -m "docs: atualiza README com instruções de deploy"
```

Tipos aceitos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

#### 6. Push e Pull Request

```bash
git push origin feature/minha-feature
```

Abra um Pull Request no GitHub com:
- **Título claro**: resumo da mudança
- **Descrição detalhada**: o que mudou e por quê
- **Screenshots** (se UI): antes e depois
- **Testes**: evidências de que funciona

### Reportar Bugs

Encontrou um problema? [Abra uma issue](https://github.com/felipemelozx/TrackMoney/issues/new) com:

```markdown
## Descrição do Bug
[Descrição clara e concisa]

## Passos para Reproduzir
1. Acesse '...'
2. Clique em '...'
3. Veja o erro

## Comportamento Esperado
[O que deveria acontecer]

## Comportamento Atual
[O que está acontecendo]

## Ambiente
- OS: [ex: Ubuntu 22.04]
- Java: [ex: 17.0.8]
- Browser: [ex: Chrome 120]

## Screenshots
[Se aplicável]
```

### Sugerir Funcionalidades

Tem uma ideia? [Abra uma issue](https://github.com/felipemelozx/TrackMoney/issues/new) com label `enhancement`:

```markdown
## Descrição da Funcionalidade
[Descrição clara da feature]

## Problema que Resolve
[Qual necessidade esta feature atende]

## Solução Proposta
[Como você imagina a implementação]

## Alternativas Consideradas
[Outras abordagens possíveis]
```

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

```
MIT License

Copyright (c) 2024 Felipe Melo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

**Resumo**: Você pode usar, copiar, modificar, mesclar, publicar, distribuir, sublicenciar e/ou vender cópias do Software, desde que inclua o aviso de copyright e esta licença.

---

## 👨‍💻 Autor

**Felipe Melo**
- 💼 Backend Engineer especializado em Java/Spring Boot
- 🎓 Focado em arquitetura de software, segurança e boas práticas
- 🚀 Entusiasta de DevOps, Clean Code e Domain-Driven Design

### 🔗 Conecte-se

- **GitHub**: [@felipemelozx](https://github.com/felipemelozx)
- **LinkedIn**: [Felipe Melo](https://www.linkedin.com/in/felipemelozx/)
- **Portfolio**: [TrackMoney (Live)](https://trackmoney.fun)
---

## 📊 Status do Projeto

| Badge | Status |
|-------|--------|
| **Build** | [![Build](https://github.com/felipemelozx/TrackMoney/workflows/TrackMoney%20Pipeline/badge.svg)](https://github.com/felipemelozx/TrackMoney/actions) |
| **Quality** | [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=felipemelozx_TrackMoney&metric=alert_status)](https://sonarcloud.io/project/overview?id=felipemelozx_TrackMoney) |
| **Coverage** | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=felipemelozx_TrackMoney&metric=coverage)](https://sonarcloud.io/project/overview?id=felipemelozx_TrackMoney) |
| **License** | [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) |

---

<div align="center">

### ⭐ Se este projeto foi útil, considere dar uma estrela!

**Desenvolvido com ❤️, ☕ e muita dedicação**

[⬆ Voltar ao topo](#trackmoney-)

</div>
