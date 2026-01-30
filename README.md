📸 Instagram Clone API - Deployment Guide
This project is a Spring Boot REST API integrated with PostgreSQL for data and MinIO for object storage. This document summarizes the configuration required to move from localhost to a Google Cloud (GCP) environment.

🛠 Tech Stack
Backend: Spring Boot 3.3.6 (Java 21)

Database: PostgreSQL

Storage: MinIO (S3 Compatible)

API Documentation: Swagger UI (SpringDoc OpenAPI)

Deployment: Docker Compose & Google Container Registry (GCR)

🚀 Critical Cloud Configurations
1. OpenAPI & Swagger Security
To fix the "Failed to fetch" and "Authorize" button issues, the OpenApiConfig.java must define the Cloud IP and the Security Scheme.

Java

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
2. Docker Compose (Cloud Optimized)
The docker-compose.yml ensures all services communicate via the Docker network while exposing the API on port 8085.

YAML

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
Step 1: Build & Push (Local Machine)
If bootBuildImage fails due to Docker Desktop pipe issues, use Jib to push directly to GCR:

PowerShell

./gradlew jib --image=gcr.io/instagramcloneproject-480610/insta-clone
Step 2: Update VM (GCP SSH)
Pull the latest image and recreate the containers:

Bash

docker-compose pull app
docker-compose up -d
Step 3: Verification
Logs: docker logs -f instagram_api (Wait for "Started" message).

Swagger: http://<YOUR_VM_IP>:8085/swagger-ui/index.html.

Important: Register a new user on the Cloud DB before trying to Login/Authenticate.

💡 Troubleshooting (Lessons Learned)
CORS Errors: Always ensure the allowedOrigins in Java includes the Cloud IP or *.

Swagger "Failed to Fetch": Occurs when the "Server" dropdown is set to localhost instead of the VM IP.

Authorize Button Inactive: Ensure @SecurityRequirement matches the name in @SecurityScheme exactly.
