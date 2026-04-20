# Deploy на Render

Для бесплатного PaaS у этого проекта лучше всего подходит Render:

- backend — как `Web Service`
- frontend — как отдельный `Web Service`
- PostgreSQL — как `Render Postgres`

В репозиторий уже добавлен [render.yaml](/c:/Users/Admin/javaProject/recipebook/render.yaml), поэтому можно использовать Blueprint.

## Что сделать

1. Запушить проект в GitHub.
2. Зайти в Render.
3. Нажать `New -> Blueprint`.
4. Подключить репозиторий.
5. Выбрать ветку `main`.
6. Убедиться, что Render нашел файл `render.yaml`.
7. Нажать `Deploy Blueprint`.

## Что создастся

- `recipebook-db` — бесплатная PostgreSQL
- `recipebook-backend` — backend на Docker
- `recipebook-frontend` — frontend на Docker

## Почему это будет работать

- backend берет порт из переменной `PORT`, что нужно для PaaS
- backend получает `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` прямо из Render Postgres
- frontend проксирует `/api` на backend через `BACKEND_ORIGIN`
- healthcheck backend: `/api/health`
- healthcheck frontend: `/`

## Важно

- бесплатные инстансы на Render подходят для учебного проекта, но могут "засыпать"
- при первом открытии после сна приложение может стартовать несколько секунд

## Официальные источники

- Render free deploy: https://render.com/docs/free
- Render web services: https://render.com/docs/web-services
- Render static sites: https://render.com/docs/static-sites
- Render blueprints: https://render.com/docs/infrastructure-as-code
- Blueprint YAML reference: https://render.com/docs/blueprint-spec
- Render health checks: https://render.com/docs/health-checks
