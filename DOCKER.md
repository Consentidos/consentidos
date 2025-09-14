# 🐳 Docker - Usage Guide for Consentidos

## 📋 Prerequisites

- Docker installed
- Docker Compose installed

## 🔧 Initial Setup (IMPORTANT)

### 1. Configure environment variables

**BEFORE running Docker**, you must configure the environment variables:

```bash
# 1. Copy the example file
cp .env.example .env

# 2. Edit the .env file with your credentials
# Change especially:
# - POSTGRES_PASSWORD=your_secure_password
# - SPRING_DATASOURCE_PASSWORD=your_secure_password
```

**⚠️ NEVER upload the `.env` file to the repository - it's already in `.gitignore`**

### 2. Verify configuration

```bash
# View the variables that will be used
docker-compose config
```

## 🚀 Basic Commands

### 1. Build the image

```bash
# Build only the application
docker build -t consentidos-app .

# Build with docker-compose
docker-compose build
```

### 2. Run with Docker Compose (Recommended)

```bash
# Run the entire application (app + database)
docker-compose up

# Run in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes (WARNING! Deletes DB data)
docker-compose down -v
```

### 3. Run only the application with Docker

```bash
# If you already have PostgreSQL running locally
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/consentidos \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  consentidos-app
```

## 🌐 Service Access

Once running with `docker-compose up`:

- **Spring Boot Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Adminer (DB Manager)**: http://localhost:8081
  - Server: `db`
  - Username: `consentidos_user`
  - Password: `consentidos_pass`
  - Database: `consentidos`

## 🛠 Useful Development Commands

### View specific logs

```bash
# Application logs only
docker-compose logs -f app

# Database logs only
docker-compose logs -f db
```

### Execute commands inside container

```bash
# Access application container
docker-compose exec app sh

# Access PostgreSQL
docker-compose exec db psql -U consentidos_user -d consentidos
```

### Rebuild after code changes

```bash
# Rebuild application only
docker-compose build app

# Rebuild and run
docker-compose up --build
```

## 🐛 Troubleshooting

### Problem: Port already in use

```bash
# Check what's using port 8080
netstat -tulpn | grep 8080

# Change port in docker-compose.yml
ports:
  - "8081:8080"  # Use port 8081 instead of 8080
```

### Problem: Database connection fails

```bash
# Verify DB is working
docker-compose exec db pg_isready -U consentidos_user

# View database logs
docker-compose logs db
```

### Problem: Application doesn't start

```bash
# View detailed logs
docker-compose logs app

# Run without docker for debugging
./gradlew bootRun
```

### Clean everything (Complete reset)

```bash
# Stop all containers
docker-compose down

# Remove built images
docker rmi consentidos-app

# Remove volumes (DELETES DATA!)
docker volume prune
```

## 📁 Docker File Structure

```
consentidos/
├── Dockerfile              # Multi-stage build
├── .dockerignore           # Files to exclude
├── docker-compose.yml      # Complete orchestration
└── src/main/resources/
    ├── application.properties                    # Default config
    ├── application-docker.properties            # Docker config
    └── application-test.properties              # Test config
```

## 🎯 Spring Boot Profiles

- **Default**: No profile (uses in-memory H2)
- **Docker**: `SPRING_PROFILES_ACTIVE=docker` (uses PostgreSQL)
- **Test**: `SPRING_PROFILES_ACTIVE=test` (uses in-memory H2)

## 🔒 Secret Management and Security

### **Local Development**

- ✅ `.env` file for credentials (NOT uploaded to Git)
- ✅ Non-root user in containers
- ✅ Health checks configured
- ✅ JVM optimizations for containers

### **Environment Variables Configuration**

```bash
# .env file (local, NOT in Git)
POSTGRES_PASSWORD=my_super_secure_password
SPRING_DATASOURCE_PASSWORD=my_super_secure_password

# You can also use system variables
export POSTGRES_PASSWORD="my_password_from_system"
docker-compose up
```

### **Production - Advanced Options**

#### **1. Docker Secrets (Docker Swarm)**

```yaml
secrets:
  db_password:
    file: ./secrets/db_password.txt

services:
  db:
    secrets:
      - db_password
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
```

#### **2. System Environment Variables**

```bash
# On production server
export POSTGRES_PASSWORD="password_from_ci_cd"
export SPRING_DATASOURCE_PASSWORD="password_from_ci_cd"
docker-compose up -d
```

#### **3. Secret Management Services**

- **AWS**: Secrets Manager, Parameter Store
- **Azure**: Key Vault
- **Google Cloud**: Secret Manager
- **HashiCorp**: Vault
- **Kubernetes**: Secrets

#### **4. CI/CD with GitHub Actions (Example)**

```yaml
# .github/workflows/deploy.yml
env:
  POSTGRES_PASSWORD: ${{ secrets.DB_PASSWORD }}
  SPRING_DATASOURCE_PASSWORD: ${{ secrets.DB_PASSWORD }}
```

### **🚨 Security Best Practices**

- ❌ **NEVER** hardcode passwords in code
- ❌ **NEVER** upload `.env` files to Git
- ✅ Use unique and complex passwords
- ✅ Rotate credentials regularly
- ✅ Use different credentials per environment
- ✅ Limit database permissions
- ✅ Use HTTPS in production
- ✅ Implement audit logs
