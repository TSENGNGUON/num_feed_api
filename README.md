# 📸 Instagram Clone API – Deployment Guide

This project is a **Spring Boot REST API** integrated with **PostgreSQL** for data persistence and **MinIO** for object storage.  
This document summarizes the configuration required to move from **localhost** to a **Google Cloud (GCP)** environment.

---

## 🛠 Tech Stack

- **Backend:** Spring Boot 3.3.6 (Java 21)
- **Database:** PostgreSQL
- **Storage:** MinIO (S3 Compatible)
- **API Documentation:** Swagger UI (SpringDoc OpenAPI)
- **Deployment:** Docker Compose & Google Container Registry (GCR)

---

## 🚀 Critical Cloud Configurations

### 1️⃣ OpenAPI & Swagger Security

To fix **"Failed to fetch"** and **Swagger Authorize button** issues,  
`OpenApiConfig.java` must define the **Cloud IP** and **Security Scheme**.

```java
@OpenAPIDefinition(
    info = @Info(title = "Instagram Clone API", version = "1.0"),
    servers = {
        @Server(description = "Cloud Production", url = "http://35.225.97.121:8085"),
        @Server(description = "Local Environment", url = "http://localhost:8080")
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
