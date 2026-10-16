# E-commerce backend

Spring Boot REST API with PostgreSQL, JWT authentication, Swagger UI and a mock payment provider.

## Run locally

Set the variables from `.env.example`, then run:

```bash
./gradlew bootRun
```

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

## Deploy on Render

The project contains a production `Dockerfile` and a `render.yaml` Blueprint.

1. Push the repository to GitHub.
2. In Render, select **New > Blueprint** and connect this repository.
3. Render will detect `render.yaml` and create the web service and PostgreSQL database.
4. When prompted, set both `FRONTEND_URL` and `CORS_ALLOWED_ORIGINS` to the frontend origin, for example `https://your-frontend.onrender.com`. Do not add a trailing slash.
5. Apply the Blueprint and wait for the health check to pass.

After deployment, Swagger UI will be at:

```text
https://your-service.onrender.com/swagger-ui/index.html
```

The application uses Render's `PORT` automatically. Database credentials and `JWT_SECRET` are supplied by the Blueprint and must not be committed to Git.
