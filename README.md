# Recipe Book

Recipe Book is organized as a fullstack-ready repository with separate backend and frontend folders.

## Structure

```text
recipebook/
|- backend/   Spring Boot REST API
|- frontend/  React + Vite frontend app
```

The backend already contains the working Spring Boot service for managing recipes, ingredients, categories, tags, units, and users.

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

The API supports CRUD operations for all core entities and bulk ingredient creation.

## Features

- Paginated REST endpoints for recipes, ingredients, categories, tags, units, and users
- Bulk ingredient creation
- Centralized exception handling with structured error responses
- OpenAPI documentation via Swagger UI
- File and console logging with Logback

## Prerequisites

For local development:

- Java 21
- PostgreSQL
- Maven Wrapper (`mvnw` / `mvnw.cmd`) is included in `backend/`

## Environment Configuration

The application reads database settings from environment variables.

Common variables:

- `DB_URL` (default: `jdbc:postgresql://localhost:5432/recipebook`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: empty)

For local backend runs, set these values in your shell environment before starting the app.

Example for Windows PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/recipebook"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
```

## Run

Start PostgreSQL, then move into `backend/` and run the application:

Linux/macOS:

```bash
cd backend
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
cd .\backend
.\mvnw.cmd spring-boot:run
```

## Docker

The repository includes container setup for:

- `postgres` database
- `backend` Spring Boot API
- `frontend` Vite app served by `nginx`

Docker Compose reads configuration from a root `.env` file. Create it from the example before the first run:

```bash
cp .env.example .env
```

Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Start the full stack from the repository root:

```bash
docker compose up --build
```

URLs after startup:

- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

Stop the stack:

```bash
docker compose down
```

For Docker-based startup, the backend receives its `DB_*` variables from `compose.yaml`, and `compose.yaml` itself reads values from the root `.env`.

## URLs

- API base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/v3/api-docs`

## API Overview

Main endpoint groups:

- `/api/recipes`
- `/api/ingredients`
- `/api/categories`
- `/api/tags`
- `/api/units`
- `/api/users`

Examples:

- `GET /api/recipes`
- `GET /api/recipes/filter/jpql`
- `POST /api/ingredients/bulk`

## Database Notes

- Database engine: PostgreSQL
- Hibernate schema mode: `update`
- SQL logging is enabled
- Default server port: `8080`

## Quality Checks

Run tests:

Linux/macOS:

```bash
cd backend
./mvnw test
```

Windows (PowerShell):

```powershell
cd .\backend
.\mvnw.cmd test
```

## Logs

Application logs are written to:

- `logs/recipebook.log`

Rolling log files are also created in the same directory.

## Notes

- `frontend/` runs as a separate client application and can be started locally or via Docker.
- Swagger UI is the easiest way to explore and test the API locally.
