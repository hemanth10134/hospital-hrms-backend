# Deploying manpower-planning-service

The service is a standard Dockerized Spring Boot app that reads all environment-specific
config from env vars (`server.port`, DB connection, CORS origins) — see
`manpower-planning-service/src/main/resources/application.yml`. No code changes should be
needed to deploy it anywhere that runs a container and gives you a Postgres database.

## Recommended: Railway

Railway is the easiest fit here: one platform for both the container and a managed
Postgres instance, minimal config, generous free tier for a project this size.

1. Push this repo to GitHub (if not already).
2. On [railway.app](https://railway.app): **New Project → Deploy from GitHub repo**, pick
   this repo.
3. Set the service's **root directory** to `manpower-planning-service` (Railway builds
   from the `Dockerfile` there automatically).
4. **Add a Postgres database** to the project (New → Database → PostgreSQL). Railway
   exposes connection variables like `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`,
   `PGPASSWORD`.
5. On the service, set environment variables (map Railway's Postgres vars to what the app
   expects):
   ```
   DB_HOST=${{Postgres.PGHOST}}
   DB_PORT=${{Postgres.PGPORT}}
   DB_NAME=${{Postgres.PGDATABASE}}
   DB_USERNAME=${{Postgres.PGUSER}}
   DB_PASSWORD=${{Postgres.PGPASSWORD}}
   CORS_ALLOWED_ORIGINS=https://<your-frontend-domain>
   ```
   Railway injects `PORT` automatically — the app already reads it (`server.port: ${PORT:8081}`).
6. Deploy. Flyway runs the migrations (`V1__init_manpower_planning_schema.sql`,
   `V2__seed_reference_data.sql`) against the fresh database automatically on first boot.
7. Health check path for Railway's settings: `/actuator/health`.

## Alternative: Render

Same shape, different platform:

1. **New → Web Service**, connect the repo, set **root directory** to
   `manpower-planning-service`, runtime **Docker** (it will use the `Dockerfile`).
2. **New → PostgreSQL** for the database; Render gives you a connection string you can
   split into `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USERNAME` / `DB_PASSWORD`, or use
   Render's "Internal Database URL" if you'd rather parse it (this app expects the
   discrete vars shown above).
3. Set `CORS_ALLOWED_ORIGINS` to your deployed frontend's URL.
4. Health check path: `/actuator/health`.

## Required environment variables (any platform)

| Variable | Purpose | Example |
|---|---|---|
| `PORT` | HTTP port the app listens on (most platforms set this for you) | `8081` |
| `DB_HOST` | Postgres host | `containers-us-west-1.railway.app` |
| `DB_PORT` | Postgres port | `5432` |
| `DB_NAME` | Database name | `hrms_manpower_planning` |
| `DB_USERNAME` | Database user | `hrms_user` |
| `DB_PASSWORD` | Database password | `••••••` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed browser origins | `https://manpower-planning.vercel.app` |

## Verifying after deploy

```bash
curl https://<backend-domain>/actuator/health
curl https://<backend-domain>/api/v1/organizations
```
Swagger UI: `https://<backend-domain>/swagger-ui.html`.

## Local Docker build/run (sanity check before deploying)

```bash
cd manpower-planning-service
docker build -t manpower-planning-service:local .
docker run --rm -p 8081:8081 \
  -e DB_HOST=host.docker.internal -e DB_PORT=5432 \
  -e DB_NAME=hrms_manpower_planning -e DB_USERNAME=hrms_user -e DB_PASSWORD=hrms_password \
  -e CORS_ALLOWED_ORIGINS=http://localhost:5173 \
  manpower-planning-service:local
```
(Run `docker compose up -d` from the repo root first so Postgres is available.)
