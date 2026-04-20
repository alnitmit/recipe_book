# GitHub CI/CD

В проекте настроены два workflow:

- `.github/workflows/ci.yml` — сборка и тесты
- `.github/workflows/deploy.yml` — развертывание на сервер и healthcheck

## Что делает CI

- запускает backend тесты через Maven Wrapper
- собирает frontend через Yarn
- проверяет, что Docker-образы backend и frontend собираются через `docker compose build`

## Что делает Deploy

- срабатывает после успешного `CI` в ветке `main`
- подключается к серверу по SSH
- обновляет код из GitHub
- выполняет `docker compose --env-file .env up -d --build`
- проверяет доступность:
  - backend: `http://127.0.0.1:${BACKEND_PORT}/api/health`
  - frontend: `http://127.0.0.1:${FRONTEND_PORT}/`

## Какие secrets нужны в GitHub

В `Settings -> Secrets and variables -> Actions` добавь:

- `DEPLOY_HOST` — IP или домен сервера
- `DEPLOY_USER` — пользователь для SSH
- `DEPLOY_SSH_KEY` — приватный SSH-ключ
- `DEPLOY_PATH` — путь к проекту на сервере, например `/opt/recipebook`

## Что должно быть на сервере

- установлен Docker и Docker Compose
- репозиторий уже склонирован в `DEPLOY_PATH`
- рядом лежит рабочий `.env`
- открыты нужные порты для frontend и backend
