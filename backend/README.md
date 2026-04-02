# SkillCert Tracker Backend

Spring Boot backend for the certification tracker.

## Endpoints

- `GET /api/certifications`
- `GET /api/certifications/summary`
- `GET /api/certifications/{id}`
- `POST /api/certifications`
- `PUT /api/certifications/{id}`
- `DELETE /api/certifications/{id}`

## Run

```powershell
powershell -ExecutionPolicy Bypass -File .\run-local.ps1
```

The fallback script compiles and starts the project using the local Maven cache because `mvn` is not available on PATH in this environment.

## Render

This backend is ready for Render deployment.

If you deploy the `backend` repository to Render:

- Render can use [`render.yaml`](C:\Users\GUDIPUDI%20AKHIL\Documents\New%20project\backend\render.yaml)
- Build command: `mvn clean package`
- Start command: `java -jar target/skillcert-tracker-backend-1.0.0.jar`

The app reads the Render-provided `PORT` automatically.
