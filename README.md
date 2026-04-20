# Recipe Book

Fullstack application for managing recipes and related entities. The project includes a Spring Boot backend, a React frontend, PostgreSQL, Docker-based local run, Render deployment, and GitHub Actions CI/CD.

## What The Project Does

- manages recipes
- manages ingredients
- manages categories
- manages tags
- manages units
- manages users
- supports recipe filtering
- exposes REST API with Swagger UI
- provides healthcheck for deployment monitoring

## Tech Stack

### Backend

- Java 21
- Spring Boot 4
- Spring Web
- Spring Data JPA
- PostgreSQL
- Bean Validation
- springdoc OpenAPI
- Maven Wrapper

### Frontend

- React 19
- TypeScript
- Vite
- Material UI
- Redux Toolkit
- RTK Query
- React Router

### DevOps

- Docker
- Docker Compose
- Render
- GitHub Actions

## Project Structure

```text
recipebook/
|-- backend/
|-- frontend/
|-- .github/workflows/
|-- compose.yaml
|-- render.yaml
`-- .env.example
```

## Main API Routes

- `/api/recipes`
- `/api/ingredients`
- `/api/categories`
- `/api/tags`
- `/api/units`
- `/api/users`
- `/api/health`

Examples:

- `GET /api/recipes`
- `GET /api/recipes/filter/jpql`
- `POST /api/recipes`
- `POST /api/ingredients/bulk`
- `GET /api/health`

## Environment Variables

Create a local `.env` file from the example:

### PowerShell

```powershell
Copy-Item .env.example .env
```

### Bash

```bash
cp .env.example .env
```

Example values:

```env
POSTGRES_DB=recipebook
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

POSTGRES_PORT=5432
BACKEND_PORT=8080
FRONTEND_PORT=3000

VITE_API_URL=/api
```

## Run Locally With Docker

From the project root:

```bash
docker compose up -d --build
```

Stop containers:

```bash
docker compose down
```

Useful local URLs:

- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Healthcheck: `http://localhost:8080/api/health`

If you need to rebuild only the frontend without Docker cache:

```bash
docker compose build --no-cache frontend
docker compose up -d frontend
```

## Run Locally Without Docker

### Backend

Start PostgreSQL first, then run:

#### PowerShell

```powershell
cd .\backend
.\mvnw.cmd spring-boot:run
```

#### Bash

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

#### PowerShell

```powershell
cd .\frontend
yarn install
yarn dev
```

#### Bash

```bash
cd frontend
yarn install
yarn dev
```

Frontend dev URL:

- `http://localhost:5173`

## Docker Services

`compose.yaml` starts three services:

- `postgres`
- `backend`
- `frontend`

The backend waits for PostgreSQL healthcheck.
The frontend communicates with the backend through nginx proxying `/api`.

## Deployment On Render

The repository contains a ready-to-use [render.yaml](render.yaml) for Render Blueprint deployment.

It creates:

- `recipebook-db`
- `recipebook-backend`
- `recipebook-frontend`

Healthcheck endpoints:

- backend: `/api/health`
- frontend: `/`

Important notes for Render:

- backend uses the `PORT` variable provided by Render
- backend database settings come from the managed PostgreSQL service
- frontend proxy settings are wired automatically through `render.yaml`
- frontend reaches the backend over Render private networking using `BACKEND_HOST` and `BACKEND_PORT`

## GitHub Actions CI/CD

Workflows are stored in `.github/workflows/`:

- `build.yaml`
- `deploy.yaml`
- `health.yaml`
- `lint.yaml`
- `pipeline.yaml`
- `push.yaml`
- `test.yaml`

Pipeline behavior:

- `pull_request` and push outside `main`: lint, test, build
- push to `main`: lint, test, build, deploy, healthcheck

Deployment is configured for Render Deploy Hooks.

### Required GitHub Secrets

- `RENDER_BACKEND_DEPLOY_HOOK_URL`
- `RENDER_FRONTEND_DEPLOY_HOOK_URL`

### Required GitHub Variables

- `RENDER_BACKEND_URL`
- `RENDER_FRONTEND_URL`

If GitHub Actions performs deployment, it is better to disable automatic deploys from Git inside Render to avoid duplicate deployments.

## Quality Checks

### Backend Tests

```powershell
cd .\backend
.\mvnw.cmd test
```

### Frontend Lint

```powershell
cd .\frontend
yarn install
yarn lint
```

### Frontend Production Build

```powershell
cd .\frontend
yarn install
yarn build
```

## Health Check

The backend exposes:

- `GET /api/health`

This endpoint is used by:

- Docker and Render health monitoring
- GitHub Actions post-deploy validation

## Useful Notes

- Hibernate schema mode is `update`
- backend runs on `8080` locally
- backend runs on Render port from `PORT`
- frontend is served by `nginx` in Docker and Render
- frontend talks to the backend through `/api`

## Troubleshooting

### Docker does not pick up frontend changes

Rebuild the frontend image without cache:

```bash
docker compose build --no-cache frontend
docker compose up -d frontend
```

### Frontend dependencies are missing locally

If TypeScript or Vite types cannot be found, reinstall frontend dependencies:

```bash
cd frontend
yarn install
```

### Render deploy succeeds but frontend cannot reach backend

Check:

- `BACKEND_URL` in the frontend Render service
- backend health endpoint availability
- GitHub Actions variables and deploy hook secrets

## License

This project is intended for educational use.
