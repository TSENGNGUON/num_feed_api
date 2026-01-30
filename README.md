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

2️⃣ Docker Compose (Cloud Optimized)

The docker-compose.yml ensures all services communicate via the Docker network
while exposing the API on port 8085.

services:
  app:
    image: gcr.io/instagramcloneproject-480610/insta-clone
    ports:
      - "8085:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/instagram_db
      - BASE_URL=http://35.225.97.121:8085
    depends_on:
      - postgres
      - minio

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: instagram_db
      POSTGRES_PASSWORD: yourpassword
📦 Deployment Workflow
Step 1️⃣ Build & Push (Local Machine)

If bootBuildImage fails due to Docker Desktop pipe issues,
use Jib to push directly to GCR:

./gradlew jib --image=gcr.io/instagramcloneproject-480610/insta-clone
Step 2️⃣ Update VM (GCP SSH)

Pull the latest image and recreate containers:
docker-compose pull app
docker-compose up -d
Step 3️⃣ Verification

Logs
docker logs -f instagram_api
(Wait for Started message)

Swagger UI
http://<YOUR_VM_IP>:8085/swagger-ui/index.html
⚠️ Important:
Register a new user in the Cloud database before attempting Login / Authentication.

💡 Troubleshooting (Lessons Learned)

CORS Errors

Ensure allowedOrigins includes the Cloud IP or *

Swagger “Failed to Fetch”

Happens when Swagger Server dropdown is set to localhost instead of VM IP

Authorize Button Not Working

@SecurityRequirement name must exactly match
@SecurityScheme name

✅ This README documents all critical cloud deployment fixes and prevents repeating the same mistakes.

---

## Why your old README looked “normal”

You were missing:
- `#` for headers
- `-` for lists
- ``` ``` for code blocks
- Language hints (`java`, `yaml`, `bash`) for syntax highlighting

---

If you want, next I can:
- 🔥 Make it **enterprise-level README**
- 📊 Add **Architecture Diagram (ASCII or image-ready)**
- 🧠 Add **Quick Start + ENV template**
- 🚀 Make a **DEPLOYMENT_CHECKLIST.md**

Just tell me.

