# TechBookStore (Legacy Stack) — Full‑stack Sample Application

TechBookStore is a **working** sample application for a technical bookstore:

- **Backend**: Spring Boot 2.3.12 (Java 8 target) REST API
- **Frontend**: React 16 + Material‑UI 4 single‑page app
- **Data**: H2 in-memory database for local development (seeded), PostgreSQL/Redis for staging/production-like profiles
- **I18n**: Japanese / English UI

This repository intentionally uses an older stack to serve as a realistic “legacy baseline” for experimentation.

## What you can do with it

- Browse/search a book catalog and view/edit book details
- Manage inventory operations (receive/sell/adjust, alerts, rotation/analysis)
- Manage customers and orders
- Explore dashboards and reports (sales/inventory/trends)
- Switch UI language (JA/EN)

## Repository layout

- `backend/`: Spring Boot API
- `frontend/`: React UI
- `start-app.sh`: start backend + frontend together (writes logs and PID files to `/tmp`)
- `status-app.sh`: check process + health status
- `stop-app.sh`: stop processes (optionally `--clean-logs`)
- `Dockerfile`: builds a single container image (frontend build + backend jar)

## Quickstart (local development)

### Prerequisites

- JDK (Java **8** recommended; project compiles to Java 8 bytecode)
- Node.js (older versions work best with Create React App 4; the Docker build uses Node 12)
- npm

If you use a newer Node.js and see `ERR_OSSL_EVP_UNSUPPORTED`, set `NODE_OPTIONS=--openssl-legacy-provider`.

### Option A: Run with the provided scripts

Start everything (backend + frontend):

```bash
./start-app.sh
```

Check status:

```bash
./status-app.sh
```

Stop:

```bash
./stop-app.sh
```

Notes:

- The scripts write logs to:

  - `/tmp/techbookstore_backend.log`
  - `/tmp/techbookstore_frontend.log`
- The scripts store PIDs in:

  - `/tmp/techbookstore_backend.pid`
  - `/tmp/techbookstore_frontend.pid`
- `start-app.sh` expects the repository to be available at **`/workspace`**.

  - If you are not using a Dev Container / containerized workspace, use Option B below (manual start).

### Option B: Run manually (works in any environment)

Backend (Spring Boot):

```bash
cd backend
./mvnw spring-boot:run
```

Frontend (React):

```bash
cd frontend
npm install
npm start
```

### URLs

- Frontend: http://localhost:3000
- Backend health: http://localhost:8080/actuator/health
- Swagger UI (springdoc): http://localhost:8080/swagger-ui/index.html
- H2 Console (dev profile): http://localhost:8080/h2-console

  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (empty)

## Architecture overview

Frontend (React) calls the backend via HTTP:

- Frontend dev server runs on `:3000`
- Frontend `proxy` forwards API calls to `http://localhost:8080` (see `frontend/package.json`)
- Backend exposes REST endpoints under `/api/v1/*`

Data and caching:

- **Dev profile** (`SPRING_PROFILES_ACTIVE=dev`, default): H2 in-memory DB and optional Redis (defaults to host `redis`)
- **Staging profile**: local PostgreSQL (`jdbc:postgresql://localhost:5432/techbookstore`)
- **Prod profile**: expects environment variables for PostgreSQL and Redis (see `backend/src/main/resources/application.yml`)

## Configuration

Backend profiles are defined in `backend/src/main/resources/application.yml`.

Common environment variables:

- `SPRING_PROFILES_ACTIVE`: `dev` (default), `staging`, `prod`
- `CORS_ALLOWED_ORIGINS`: default `http://localhost:3000`

Dev profile (Redis is optional):

- `REDIS_HOST` (default: `redis`)
- `REDIS_PORT` (default: `6379`)

If you do not have Redis available locally, force an in-memory cache with:

- `SPRING_CACHE_TYPE=simple` (equivalent to `spring.cache.type=simple`)

Staging profile (PostgreSQL):

- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: `postgres`)

Prod profile variables are currently named with an `AZURE_*` prefix (from the original deployment setup):

- `AZURE_POSTGRESQL_HOST`, `AZURE_POSTGRESQL_DATABASE`, `AZURE_POSTGRESQL_USERNAME`, `AZURE_POSTGRESQL_PASSWORD`
- `AZURE_REDIS_HOST`, `AZURE_REDIS_KEY`

## Data model and seed data

In the default dev profile, the database is created on startup and seeded from:

- `backend/src/main/resources/data.sql`

## API overview

High-level API groups (all under `/api/v1`):

- `/books`: book catalog operations
- `/inventory`: inventory operations and analytics
- `/reports`: dashboards and reporting endpoints

API docs are exposed via springdoc-openapi (OpenAPI 3) at `/swagger-ui/index.html`.

## Testing

Backend:

```bash
cd backend
./mvnw test
```

Frontend:

```bash
cd frontend
npm test
```

CI-style frontend tests:

```bash
cd frontend
npm run test:ci
```

I18n validation helper:

- `validate-i18n.sh` exists, but it is currently hardcoded for a GitHub Actions runner path and may not work locally without adjustment.

## Docker

The root `Dockerfile` builds:

1) the frontend static build, then
2) the backend jar, and
3) runs the backend on port `8080`.

Important:

- The container sets `SPRING_PROFILES_ACTIVE=prod` by default.
- In `prod`, the backend expects PostgreSQL/Redis environment variables (see “Configuration”).

## Security and production notes

This repository is a sample app.

- Some configuration (including security) is intentionally simplified for demo and local use.
- Review and harden settings before using it as a production template.

## License

Released under the [MIT license](https://gist.githubusercontent.com/shinyay/56e54ee4c0e22db8211e05e70a63247e/raw/f3ac65a05ed8c8ea70b653875ccac0c6dbc10ba1/LICENSE)

## Author

- GitHub: <https://github.com/shinyay>
- Twitter: <https://twitter.com/yanashin18618>
- Mastodon: <https://mastodon.social/@yanashin>
