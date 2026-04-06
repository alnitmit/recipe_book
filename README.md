# Recipe Book

Recipe Book is a Spring Boot REST API for managing recipes, ingredients, categories, tags, units, and users.

## Stack

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA (Hibernate)
- PostgreSQL
- Bean Validation
- OpenAPI / Swagger UI (`springdoc-openapi`)
- Maven

## Domain Model

Main entities:

- Recipe
- Ingredient
- Category
- Tag
- Unit
- User

The API supports CRUD operations for all core entities, bulk ingredient creation, and asynchronous recipe creation.

## Features

- Paginated REST endpoints for recipes, ingredients, categories, tags, units, and users
- Bulk ingredient creation with transactional and non-transactional modes
- Async recipe creation with task status and metrics endpoints
- Centralized exception handling with structured error responses
- OpenAPI documentation via Swagger UI
- File and console logging with Logback

## Prerequisites

For local development:

- Java 21
- PostgreSQL
- Maven Wrapper (`mvnw` / `mvnw.cmd`) is already included in the repository

## Environment Configuration

The application reads database settings from environment variables.

Common variables:

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/recipebook`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: empty)

You can provide them through your shell environment or a local `.env` file if your setup loads it.

Example for Windows PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/recipebook"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
```

## Run

Start PostgreSQL, then run the application:

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

## URLs

- API base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/v3/api-docs`

## API Overview

Main endpoint groups:

- `/api/recipes`
- `/api/recipes/async`
- `/api/ingredients`
- `/api/categories`
- `/api/tags`
- `/api/units`
- `/api/users`

Examples:

- `GET /api/recipes`
- `GET /api/recipes/filter/jpql`
- `POST /api/recipes/async`
- `GET /api/recipes/async/status/{taskId}`
- `GET /api/recipes/async/metrics`
- `POST /api/ingredients/bulk`
- `POST /api/ingredients/bulk/no-transaction`

## Database Notes

- Database engine: PostgreSQL
- Hibernate schema mode: `update`
- SQL logging is enabled
- Default server port: `8080`

## Quality Checks

Run tests:

Linux/macOS:

```bash
./mvnw test
```

Windows (PowerShell):

```powershell
.\mvnw.cmd test
```

## Logs

Application logs are written to:

- `logs/recipebook.log`

Rolling log files are also created in the same directory.

## Notes

- This repository currently contains a backend service only.
- Swagger UI is the easiest way to explore and test the API locally.
- Performance artifacts under `performance/` are kept locally and are ignored by Git.
