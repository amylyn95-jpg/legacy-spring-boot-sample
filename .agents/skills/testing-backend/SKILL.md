---
name: testing-backend
description: How to run and end-to-end test the TechBookStore Spring Boot backend locally (dev profile, Swagger UI, H2 console, seeded data, known issues).
---

# Testing the TechBookStore backend

> Written against the Java 17 / Spring Boot 3.5 migration branch (PR #1). On `main` (Java 8, Boot 2.3, Springfox, Node 12) the JDK, springdoc URLs, Node 18 frontend stage, `data.sql` fixes, the `NoResourceFoundException` 404 handler and the JSON cache config described below do not exist yet — check out the migration branch before using this guide.

## Run it
- Build from `backend/` (that is where `mvnw` lives): `cd backend && MVNW_REPOURL=... ./mvnw -B clean verify`.
- JDK 17 is the default `java` (`/usr/lib/jvm/java-17-openjdk-amd64`); JDK 8 is at `/usr/lib/jvm/java-8-openjdk-amd64` if you need to compare against the pre-migration baseline.
- Maven Central may rate-limit this VM (HTTP 429). Use the GCS mirror: `MVNW_REPOURL=https://maven-central.storage-download.googleapis.com/maven2 ./mvnw ...`. Prefer the prebuilt jar when it is current: `backend/target/techbookstore-backend-0.0.1-SNAPSHOT.jar`.
- Optional dependencies run in Docker and may be stopped after a VM restart: `docker start devin-redis devin-pg12`.
- Start (dev = in-memory H2 + `data.sql` seed, port 8080), freeing the port first:
  ```bash
  fuser -k 8080/tcp 2>/dev/null; \
  SPRING_PROFILES_ACTIVE=dev REDIS_HOST=localhost \
    java -jar backend/target/techbookstore-backend-0.0.1-SNAPSHOT.jar > /tmp/backend.log 2>&1 &
  ```
  Wait for `Started TechBookStoreApplication` in `/tmp/backend.log`; keep that log for stack traces (API errors are masked as `INTERNAL_ERROR`).

## UI surfaces to drive
- Swagger UI (springdoc): `http://localhost:8080/swagger-ui/index.html`; OpenAPI JSON at `/v3/api-docs`. Deep-link to an operation with `#/<tag>/<operationId>` (e.g. `#/order-controller/getOrderById`) instead of scrolling.
- H2 console: `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:testdb`, user `sa`, empty password (the stock dev-profile in-memory H2 defaults — keep port 8080 bound to localhost, since the app ships with `permitAll` and no auth). It renders in a frame — a blank/refused frame means the Security config's same-origin frame options regressed.
- The React frontend IS usable as a test surface when the Docker image is built (Dockerfile frontend stage on `node:18-alpine`): `docker build -t techbookstore:java17 .` then `docker run -d --name tbs-j17 --network host -e SPRING_PROFILES_ACTIVE=dev -e REDIS_HOST=localhost techbookstore:java17` serves the SPA at `/` and Swagger at `/swagger-ui/index.html` on the same origin. 9 Jest tests (BookEditForm, ReceiveStockDialog) fail pre-existing — ignore them.
- The container has **no SPA fallback**: reloading or deep-linking a client-side route (e.g. `/reports/inventory`) returns the backend JSON 404 (`No static resource ...`). Always navigate from `/` via the left nav instead of typing the route into the address bar.
- `frontend/src/components/IntegratedDashboard.js` is the only frontend caller of `/api/v1/inventory/integrated/realtime-dashboard`, and it is **not routed** in `App.js`, so no UI screen reaches the integrated endpoints — drive them from Swagger UI.

## Seeded data (dev profile)
5 books, 3 customers, 4 orders, inventory rows (book 1: store 25 / warehouse 100, `A-001`), `BOOK_AUTHORS.AUTHOR_TYPE='AUTHOR'`. New rows must therefore start at customer id 4 / order id 5 — a PK collision or reused id points at broken identity `RESTART WITH` handling in `data.sql`.

## Useful payloads
```json
POST /api/v1/customers
{"customerType":"INDIVIDUAL","name":"テスト太郎","email":"t@example.com","phone":"090-1111-2222","gender":"MALE"}

POST /api/v1/orders
{"customerId":4,"type":"ONLINE","paymentMethod":"CREDIT_CARD","orderItems":[{"bookId":1,"quantity":2}]}
```
Invalid payloads should give HTTP 400 with the `GlobalExceptionHandler` shape (`code: VALIDATION_ERROR` + per-field messages), not 500.

## Gotchas
- There is no `/api/v1/reports/analytics`. Real report paths: `/reports/dashboard/kpis|trends|alerts`, `/reports/sales[/trend|/ranking]`, `/reports/inventory[/enhanced|/turnover|/reorder]`, `/reports/customers[/rfm|/segments]`. `/reports/sales` requires `startDate`/`endDate` (else 400 `MISSING_PARAMETER`).
- Unmapped paths (e.g. `/api/v1/nonexistent`) once returned 500 on Spring Boot 3 (`NoResourceFoundException` swallowed by the generic handler); with the explicit `@ExceptionHandler(NoResourceFoundException.class)` they return 404 `{"code":"NOT_FOUND",...}`. If you see 500 again, that handler has regressed.
- `/api/v1/i18n/messages` picks the locale from the `Accept-Language` header only (defaults to Japanese; `en*` → English). A `?locale=` query param has no effect, and Chrome sends `en-US`, so the browser shows English.
- Redis-cache serialization (`NotSerializableException: InventoryReportDto`) was fixed in `IntegratedCacheConfiguration` (named caches reuse `cacheConfiguration()` with `GenericJackson2JsonRedisSerializer` + `JavaTimeModule`). Verify with `docker exec devin-redis redis-cli --scan` (keys `dashboardData::...`, `integratedAnalysis::...`), `GET <key>` should be JSON starting `{"@class":...}` (binary = regression), and TTLs ≤60s / ≤600s respectively. Clear only this app's cache keys before a cold-vs-warm comparison (never `FLUSHALL` — the container may be shared): `docker exec devin-redis sh -c "redis-cli --scan --pattern '*Analysis::*' | xargs -r redis-cli DEL"` (repeat for `dashboardData::*`, `*Report::*`, `performanceMetrics::*`); a cache hit shows a stale inner `systemHealth.lastUpdated` while the wrapper timestamp advances.
- `POST /api/v1/inventory/integrated/batch-optimization` is **not idempotent**: it succeeds once per fresh DB, and a repeat call the same day returns 500 (`Batch optimization failed`) from a unique-index violation on `ABC_XYZ_ANALYSIS(BOOK_ID, ANALYSIS_DATE)`. Restart the app/container (fresh H2) between batch tests; don't mistake this for a cache/serialization bug. Confirmed pre-existing: the same 200-then-500 sequence reproduces on the Java 8 / Boot 2.3.12 baseline.
- `/reports/inventory` in the React UI may render only its title/filters with no report body even though `GET /api/v1/reports/inventory` returns populated JSON. This is a pre-existing frontend bug, not a build/API problem: `reportsApi.getInventoryReport()` already returns `response.data`, and `InventoryReport.js` unwraps it a second time, so `reportData` stays undefined and the body is gated off with no console error.

## Devin Secrets Needed
None.
