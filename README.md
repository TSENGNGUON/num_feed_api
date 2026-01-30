# 📸 Instagram Clone API – Deployment Guide

This project is a Spring Boot REST API using PostgreSQL for relational data and MinIO for object storage.  
This README documents how the application is deployed from localhost to a cloud VM in a secure and repeatable way.

---

## 🛠 Tech Stack

- ☕ Spring Boot 3.3.6 (Java 21)
- 🐘 PostgreSQL
- 🪣 MinIO (S3 Compatible)
- 📘 Swagger UI (SpringDoc OpenAPI)
- 🐳 Docker & Docker Compose
- ☁️ Google Container Registry

---

## 🔐 OpenAPI & Swagger Configuration

Cloud deployments must not rely on hardcoded localhost URLs.  
The API base URL is injected using environment variables to avoid CORS and networking issues.

```java
@OpenAPIDefinition(
    info = @Info(title = "Instagram Clone API", version = "1.0"),
    servers = {
        @Server(description = "Production", url = "${BASE_URL}"),
        @Server(description = "Local", url = "http://localhost:8080")
    },
    security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {}
