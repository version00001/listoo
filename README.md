# How to start

mvn spring-boot:run "-Dspring-boot.run.profiles=local"

# How to run tests

mvn test "-Dspring-boot.run.profiles=test"

# Swagger UI at:

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs

# Start postgresql server

docker-compose up -d

# Recreate postgresql server

Removes everything, including database storage.

```sql
docker-compose down -v
```

Stops & removes containers but keeps database data.

```sql
docker-compose down
```

# JWT secret generation

openssl rand -base64 129 | tr -d '\n'