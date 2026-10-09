# TrackMoney — instruções para agentes

## Projeto

TrackMoney é uma API de gestão financeira pessoal construída com Java 17 e Spring Boot 3.4.5. O projeto usa Maven, PostgreSQL, Redis, Flyway e JWT.

## Comandos

```bash
# Subir PostgreSQL e Redis
docker compose -f docker/docker-compose.yaml up -d

# Executar com Swagger habilitado (perfil dev)
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Executar todos os testes
./mvnw test

# Build, testes, cobertura e Checkstyle
./mvnw clean verify

# Executar uma classe de teste específica
./mvnw test -Dtest="AccountServiceTest"
```

## Arquitetura

O código Java fica em `src/main/java/fun/trackmoney/` e é organizado por camada:

- `controller/`: endpoints REST; recebem e retornam DTOs, nunca entidades JPA.
- `service/`: regras de negócio e transações.
- `repository/`: persistência Spring Data JPA; projeções em `repository/projection/`.
- `entity/`: entidades JPA.
- `dto/`: contratos da API, agrupados por domínio.
- `mapper/`: conversões entre DTOs e entidades com MapStruct.
- `config/` e `infra/`: configuração da aplicação e integrações (segurança, JWT, e-mail e Redis).
- `exception/`, `enums/` e `utils/`: componentes compartilhados.
- `seed/`: geração de dados de desenvolvimento.

Os testes ficam em `src/test/java/`, organizados de forma semelhante ao código principal. Migrações versionadas do banco ficam em `src/main/resources/db/migration/`.

## Diretrizes

- Preserve a arquitetura em camadas existente; não mova classes entre camadas sem necessidade da tarefa.
- Use DTOs na fronteira HTTP e MapStruct para mapeamento. Não exponha entidades nos controllers.
- Mantenha regras de negócio e validações de domínio nos serviços; use Bean Validation nos DTOs de entrada.
- Use exceções específicas do domínio em vez de `RuntimeException` genérica.
- Crie migração Flyway para mudanças de schema e testes para mudanças de comportamento.
- Não adicione dependências ou altere configurações sem necessidade explícita.
- Antes de concluir mudanças, execute os testes apropriados; para alterações amplas, use `./mvnw clean verify`.

## Configuração

- Configuração base: `src/main/resources/application.yaml`.
- Perfis: `application-dev.yaml` e `application-prod.yaml`.
- Variáveis locais: configure `.env` a partir de `.env.example`; nunca versione segredos.
- Prefixo padrão da API: `/api/v1`.
