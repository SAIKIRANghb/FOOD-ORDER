# Hungrezy Food Ordering Backend

Spring Boot 3 backend scaffold for the Hungrezy food delivery system, based on the provided LLD.

## Stack

- Java 17
- Spring Boot 3.3
- PostgreSQL 16
- Redis 7
- Spring Security + JWT
- Spring WebSocket + STOMP
- Flyway
- Swagger UI

## Run Locally

Start PostgreSQL and Redis:

```bash
docker compose up -d postgres redis
```

Run the backend:

```bash
mvn spring-boot:run
```

The local defaults expect the Docker Compose database:

```text
DB_URL=jdbc:postgresql://localhost:5432/hungrezy
DB_USERNAME=hungrezy
DB_PASSWORD=hungrezy
```

If you previously started Postgres with a different persistent volume and see
`FATAL: database "hungrezy" does not exist`, recreate the local database volume:

```bash
docker compose down -v
docker compose up -d postgres redis
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## Main Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `GET /api/users/profile`
- `POST /api/users/address`
- `POST /api/restaurants`
- `GET /api/restaurants/search`
- `GET /api/restaurants/{id}/menu`
- `POST /api/restaurants/{restaurantId}/menu`
- `POST /api/orders`
- `GET /api/orders/my-orders`
- `GET /api/orders/{id}/track`
- `PATCH /api/orders/{id}/status`
- `POST /api/reviews`
- `POST /api/contact`
- `GET /api/announcements`

Order updates are published over STOMP at:

```text
/topic/orders/{orderId}
```
