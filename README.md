# E-Commerce Backend API

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13%2B-4169E1.svg)](https://www.postgresql.org/)
[![Gradle](https://img.shields.io/badge/Gradle-Wrapper-02303A.svg)](https://gradle.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED.svg)](https://www.docker.com/)

A deployment-ready REST API for an online store, built with Spring Boot. It provides JWT authentication, a public product catalog, shopping carts, order management, mock payments, role-based administration and interactive OpenAPI documentation.

## Features

- User registration and login with JWT authentication
- `CUSTOMER` and `ADMIN` role-based authorization
- Public product and category catalog
- Product search and category filtering
- Persistent shopping cart management
- Order creation, history and cancellation
- Pluggable payment provider with a mock implementation
- Admin dashboard and management endpoints
- PostgreSQL schema management and seed data with Liquibase
- Swagger UI and OpenAPI documentation
- Health checks for deployment monitoring
- Docker and Render Blueprint deployment configuration
- Controller, service, security and OpenAPI tests

## Technology stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4 |
| API | Spring Web MVC, Jakarta Validation |
| Security | Spring Security, JWT, BCrypt |
| Database | PostgreSQL, Spring Data JPA |
| Migrations | Liquibase |
| Documentation | Springdoc OpenAPI / Swagger UI |
| Testing | JUnit 5, Spring Boot Test, MockMvc, Mockito |
| Build | Gradle Wrapper |
| Deployment | Docker, Render Blueprint |

## API overview

| Module | Base path | Access |
| --- | --- | --- |
| Authentication | `/api/auth` | Public |
| Products | `/api/products` | Public read access |
| Categories | `/api/categories` | Public read access |
| Cart | `/api/cart` | Authenticated users |
| Orders | `/api/orders` | Authenticated users |
| Payments | `/api/payments` | Authenticated users |
| Admin dashboard | `/api/admin/dashboard` | Admin only |
| Admin products | `/api/admin/products` | Admin only |
| Admin categories | `/api/admin/categories` | Admin only |
| Admin orders | `/api/admin/orders` | Admin only |
| Admin payments | `/api/admin/payments` | Admin only |
| Admin users | `/api/admin/users` | Admin only |
| Health check | `/actuator/health` | Public |

The complete request and response schemas are available through Swagger UI.

## Getting started

### Prerequisites

- Java 17 or newer
- PostgreSQL 13 or newer (only when running with the `prod` profile)

Local development uses an in-memory H2 database, so PostgreSQL and Docker are not required. Render uses PostgreSQL and builds the included Dockerfile automatically.

### Environment variables

Copy the values from `.env.example` into your IDE run configuration or export them in the terminal.

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `DB_HOST` | No | `localhost` | PostgreSQL host |
| `DB_PORT` | No | `5432` | PostgreSQL port |
| `DB_NAME` | No | `javacommerce` | Database name |
| `DB_USERNAME` | No | `postgres` | Database username |
| `DB_PASSWORD` | Yes | — | Database password |
| `JWT_SECRET` | Yes | — | Base64-encoded key (at least 32 decoded bytes) or a random text secret of at least 32 bytes |
| `JWT_EXPIRATION` | No | `86400000` | Token lifetime in milliseconds |
| `FRONTEND_URL` | No | `http://localhost:4200` | Frontend URL used by mock checkout |
| `CORS_ALLOWED_ORIGINS` | No | `http://localhost:4200` | Comma-separated allowed origins |
| `PORT` | No | `8080` | HTTP server port; supplied automatically by Render |

Generate a suitable development JWT secret with:

```bash
openssl rand -base64 48
```

Never commit real database passwords or JWT secrets.

### Run locally

Start the application without additional environment variables:

```bash
./gradlew bootRun
```

The default `local,mock-payment` profiles use an in-memory H2 database and a development-only JWT key. Liquibase creates and seeds the schema automatically. The local application is available at `http://localhost:8080`.

Production uses the `prod,mock-payment` profiles and requires the PostgreSQL and JWT environment variables listed above.

## Authentication example

Register a customer:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Ivan",
    "lastName": "Ivanov",
    "email": "ivan@example.com",
    "password": "password123"
  }'
```

Log in:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "ivan@example.com",
    "password": "password123"
  }'
```

Use the returned token for protected endpoints:

```text
Authorization: Bearer <token>
```

## Swagger / OpenAPI

With the application running, open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

To test protected endpoints in Swagger, select **Authorize** and enter the JWT token.

## Tests

Run the complete test suite:

```bash
./gradlew test
```

The test suite covers authentication, products, carts, orders, payments, admin authorization and OpenAPI generation.

## Docker

Build the production image:

```bash
docker build -t ecommerce-backend .
```

The container listens on the port provided through `PORT` and runs as a non-root user.

## Deploy to Render

The repository includes a [Render Blueprint](render.yaml) that creates both the Docker web service and PostgreSQL database.

1. Fork or push this repository to GitHub.
2. Open the [Render Dashboard](https://dashboard.render.com/blueprints).
3. Select **New Blueprint** and connect the repository.
4. Use branch `main` and Blueprint path `render.yaml`.
5. When prompted, set `FRONTEND_URL` and `CORS_ALLOWED_ORIGINS` to the frontend origin.
6. Apply the Blueprint and wait for `/actuator/health` to report a healthy deployment.

If the frontend has not been deployed yet, both values can temporarily be set to `http://localhost:4200`.

After deployment:

```text
API:        https://<service-name>.onrender.com
Swagger UI: https://<service-name>.onrender.com/swagger-ui/index.html
Health:     https://<service-name>.onrender.com/actuator/health
```

Render supplies the database credentials, application port and generated JWT secret through environment variables. No production secrets are stored in the repository.

## Project structure

```text
src/main/java/com/shop/ecommerce
├── admin       # Admin dashboard and management APIs
├── auth        # Registration, login, JWT and roles
├── cart        # Shopping cart operations
├── category    # Public category catalog
├── common      # Security, CORS, OpenAPI and exception handling
├── order       # Order lifecycle
├── payment     # Payment API and provider abstraction
└── product     # Public product catalog and search

src/main/resources
├── application.yml
└── db/changelog # Liquibase migrations and seed data
```

## Payment status

The active `mock-payment` profile provides a local/demo checkout flow and does not process real money. A real payment provider such as Stripe should be added as a separate Spring profile before accepting production payments.
