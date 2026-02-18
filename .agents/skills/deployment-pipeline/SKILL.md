---
name: deployment-pipeline
description: Docker and CI/CD deployment pipeline patterns for multi-service Kotlin Spring Boot + React TypeScript projects. This skill should be used when configuring Docker Compose for local development, writing multi-stage Dockerfiles, setting up GitHub Actions workflows for multi-module builds, implementing Blue-Green deployment, or managing environment-specific configurations. Triggers on tasks involving Dockerfile, docker-compose.yml, GitHub Actions workflows, deployment scripts, health checks, nginx configuration, or cloud infrastructure setup.
metadata:
  author: jin
  version: "1.0.0"
---

# Deployment Pipeline Patterns

Comprehensive guide for containerizing, building, and deploying multi-service Kotlin Spring Boot + React TypeScript applications. Contains 40 rules across 5 categories, covering local development, Docker builds, CI/CD automation, deployment strategies, and monitoring.

## When to Apply

Reference these guidelines when:
- Setting up or modifying Docker Compose for local development
- Writing or optimizing Dockerfiles for backend or frontend services
- Configuring GitHub Actions CI/CD workflows
- Implementing deployment strategies (Blue-Green, rolling)
- Setting up nginx as reverse proxy
- Configuring health checks and readiness probes
- Managing secrets and environment variables in CI/CD

## Rule Categories by Priority

| Priority | Category | Impact | Prefix |
|----------|----------|--------|--------|
| 1 | Docker Compose Development | CRITICAL | `compose-` |
| 2 | Dockerfile Optimization | CRITICAL | `docker-` |
| 3 | GitHub Actions CI/CD | HIGH | `ci-` |
| 4 | Deployment Strategies | HIGH | `deploy-` |
| 5 | Monitoring & Health Checks | MEDIUM | `health-` |

---

## Detailed Rules

### 1. Docker Compose Development (CRITICAL)

#### `compose-full-stack-dev`
**Define complete local dev environment with all services**

✅ Good: Full-stack development compose
```yaml
# docker-compose.yml
services:
  # ── Database ──────────────────────────────────────────
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: lostark
      POSTGRES_USER: dev
      POSTGRES_PASSWORD: dev
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./db/init:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U dev -d lostark"]
      interval: 5s
      timeout: 3s
      retries: 5

  # ── Cache ─────────────────────────────────────────────
  redis:
    image: redis:7-alpine
    command: redis-server --requirepass dev --maxmemory 256mb --maxmemory-policy allkeys-lru
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "dev", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5

  # ── Backend Services ──────────────────────────────────
  integration-service:
    build:
      context: .
      dockerfile: integration-service/Dockerfile
      target: development
    environment:
      SPRING_PROFILES_ACTIVE: local
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/lostark
      SPRING_DATASOURCE_USERNAME: dev
      SPRING_DATASOURCE_PASSWORD: dev
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD: dev
    ports:
      - "8081:8080"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    volumes:
      - ./integration-service/src:/app/src
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s

  processor-service:
    build:
      context: .
      dockerfile: processor-service/Dockerfile
      target: development
    environment:
      SPRING_PROFILES_ACTIVE: local
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/lostark
      SPRING_DATASOURCE_USERNAME: dev
      SPRING_DATASOURCE_PASSWORD: dev
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD: dev
    ports:
      - "8082:8080"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
      integration-service:
        condition: service_healthy

  # ── Reverse Proxy ─────────────────────────────────────
  nginx:
    image: nginx:1.27-alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx/dev.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - integration-service
      - processor-service

volumes:
  postgres-data:
  redis-data:
```

---

#### `compose-profiles-separation`
**Use Docker Compose profiles for optional services**

✅ Good: Profiles for optional development tools
```yaml
services:
  # Always running
  postgres:
    # ...

  # Only with monitoring profile
  prometheus:
    image: prom/prometheus:latest
    profiles: ["monitoring"]
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    profiles: ["monitoring"]
    ports:
      - "3001:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    volumes:
      - grafana-data:/var/lib/grafana

  # Only with debug profile
  pgadmin:
    image: dpage/pgadmin4:latest
    profiles: ["debug"]
    ports:
      - "5050:80"
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@local.dev
      PGADMIN_DEFAULT_PASSWORD: admin
```

```bash
# Start core services
docker compose up

# Start with monitoring
docker compose --profile monitoring up

# Start everything
docker compose --profile monitoring --profile debug up
```

---

#### `compose-healthcheck-dependencies`
**Use healthchecks with `depends_on: condition` for proper startup order**

❌ Bad: Only `depends_on` without health check
```yaml
integration-service:
  depends_on:
    - postgres  # Only waits for container start, NOT readiness
```

✅ Good: Health-based dependency
```yaml
integration-service:
  depends_on:
    postgres:
      condition: service_healthy  # Waits until pg_isready passes
    redis:
      condition: service_healthy
```

---

#### `compose-env-file`
**Use .env files for environment variables, never hardcode secrets**

✅ Good: Environment file management
```yaml
# docker-compose.yml
services:
  postgres:
    env_file:
      - .env.local
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
```

```bash
# .env.local (gitignored)
DB_NAME=lostark
DB_USER=dev
DB_PASSWORD=dev
REDIS_PASSWORD=dev

# .env.example (committed, template for team)
DB_NAME=lostark
DB_USER=
DB_PASSWORD=
REDIS_PASSWORD=
```

```gitignore
# .gitignore
.env.local
.env.production
!.env.example
```

---

### 2. Dockerfile Optimization (CRITICAL)

#### `docker-multistage-backend`
**Multi-stage build with dependency caching for Kotlin/Gradle**

✅ Good: Optimized multi-stage Dockerfile for Spring Boot
```dockerfile
# integration-service/Dockerfile

# ── Stage 1: Dependencies (cached separately) ──────────
FROM eclipse-temurin:21-jdk-alpine AS deps
WORKDIR /app

# Copy only dependency-related files first
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY gradle/libs.versions.toml gradle/
COPY build-logic/ build-logic/
COPY core/domain/build.gradle.kts core/domain/
COPY core/common/build.gradle.kts core/common/
COPY integration-service/build.gradle.kts integration-service/

# Download dependencies (cached if build files unchanged)
RUN ./gradlew :integration-service:dependencies --no-daemon

# ── Stage 2: Build ──────────────────────────────────────
FROM deps AS builder
WORKDIR /app

# Copy source code
COPY core/ core/
COPY integration-service/src/ integration-service/src/

# Build the bootJar
RUN ./gradlew :integration-service:bootJar --no-daemon -x test

# ── Stage 3: Development (with hot reload) ──────────────
FROM eclipse-temurin:21-jdk-alpine AS development
WORKDIR /app
COPY --from=builder /app/ .
CMD ["./gradlew", ":integration-service:bootRun", "--no-daemon"]

# ── Stage 4: Production runtime ─────────────────────────
FROM eclipse-temurin:21-jre-alpine AS production

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

COPY --from=builder /app/integration-service/build/libs/*.jar app.jar

# Security: run as non-root
USER app

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
```

---

#### `docker-multistage-frontend`
**Multi-stage build for React frontend with nginx**

✅ Good: Optimized frontend Dockerfile
```dockerfile
# frontend/Dockerfile

# ── Stage 1: Dependencies ───────────────────────────────
FROM node:22-alpine AS deps
WORKDIR /app
COPY package.json pnpm-lock.yaml ./
RUN corepack enable && pnpm install --frozen-lockfile

# ── Stage 2: Build ──────────────────────────────────────
FROM deps AS builder
WORKDIR /app
COPY . .

ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN pnpm run build

# ── Stage 3: Production ─────────────────────────────────
FROM nginx:1.27-alpine AS production

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx/frontend.conf /etc/nginx/conf.d/default.conf

# Copy built assets
COPY --from=builder /app/dist /usr/share/nginx/html

# Security headers and non-root
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

```nginx
# nginx/frontend.conf
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Health check endpoint
    location /health {
        return 200 'ok';
        add_header Content-Type text/plain;
    }

    # API proxy to backend
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # SPA fallback - serve index.html for all routes
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets aggressively
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff2?)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # Gzip
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml;
    gzip_min_length 1000;
}
```

---

#### `docker-layer-ordering`
**Order Dockerfile layers from least to most frequently changing**

✅ Good: Optimal layer ordering
```
1. Base image                    ← rarely changes
2. System packages               ← rarely changes
3. Dependency manifest files     ← changes when deps change
4. Install dependencies          ← cached until deps change
5. Copy source code              ← changes frequently
6. Build                         ← runs on source changes
7. Runtime configuration         ← rarely changes
```

---

#### `docker-ignore`
**Use .dockerignore to minimize build context**

✅ Good: Comprehensive .dockerignore
```dockerignore
# .dockerignore
.git
.github
.idea
.vscode
*.md
!README.md

# Build outputs
**/build/
**/dist/
**/node_modules/
**/.gradle/

# Environment files
.env*
!.env.example

# Test files
**/*test*
**/*Test*
**/__tests__/

# Docker files (avoid recursive)
**/Dockerfile*
**/docker-compose*
```

---

### 3. GitHub Actions CI/CD (HIGH)

#### `ci-complete-pipeline`
**Define complete CI/CD pipeline with test → build → deploy stages**

✅ Good: Complete multi-module pipeline
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  REGISTRY: ghcr.io
  IMAGE_PREFIX: ghcr.io/${{ github.repository }}
  JAVA_VERSION: "21"
  NODE_VERSION: "22"

jobs:
  # ── Change Detection ────────────────────────────────
  changes:
    runs-on: ubuntu-latest
    outputs:
      backend: ${{ steps.filter.outputs.backend }}
      frontend: ${{ steps.filter.outputs.frontend }}
      integration: ${{ steps.filter.outputs.integration }}
      processor: ${{ steps.filter.outputs.processor }}
    steps:
      - uses: actions/checkout@v4
      - uses: dorny/paths-filter@v3
        id: filter
        with:
          filters: |
            backend:
              - 'core/**'
              - 'market-data/**'
              - 'build-logic/**'
              - 'gradle/**'
              - '*.gradle.kts'
            integration:
              - 'integration-service/**'
              - 'core/**'
            processor:
              - 'processor-service/**'
              - 'core/**'
            frontend:
              - 'frontend/**'

  # ── Backend Tests ───────────────────────────────────
  backend-test:
    needs: changes
    if: needs.changes.outputs.backend == 'true' || needs.changes.outputs.integration == 'true' || needs.changes.outputs.processor == 'true'
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 5s
          --health-timeout 3s
          --health-retries 5
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: ${{ env.JAVA_VERSION }}
      - uses: gradle/actions/setup-gradle@v4
      - name: Run tests
        run: ./gradlew test --build-cache --parallel
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/test
          SPRING_DATASOURCE_USERNAME: test
          SPRING_DATASOURCE_PASSWORD: test
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: backend-test-results
          path: "**/build/reports/tests/"

  # ── Backend Static Analysis ─────────────────────────
  backend-lint:
    needs: changes
    if: needs.changes.outputs.backend == 'true'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: ${{ env.JAVA_VERSION }}
      - uses: gradle/actions/setup-gradle@v4
      - run: ./gradlew detekt ktlintCheck --build-cache

  # ── Frontend Tests ──────────────────────────────────
  frontend-test:
    needs: changes
    if: needs.changes.outputs.frontend == 'true'
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v4
        with:
          version: 9
      - uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: pnpm
          cache-dependency-path: frontend/pnpm-lock.yaml
      - run: pnpm install --frozen-lockfile
      - run: pnpm run type-check
      - run: pnpm run lint
      - run: pnpm run test -- --run

  # ── Build & Push Docker Images ──────────────────────
  build-backend:
    needs: [backend-test, backend-lint]
    if: github.ref == 'refs/heads/main' && (needs.changes.outputs.integration == 'true' || needs.changes.outputs.processor == 'true' || needs.changes.outputs.backend == 'true')
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service: [integration-service, processor-service]
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: ${{ env.JAVA_VERSION }}
      - uses: gradle/actions/setup-gradle@v4
      - name: Build jar
        run: ./gradlew :${{ matrix.service }}:bootJar --build-cache -x test
      - uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - uses: docker/build-push-action@v6
        with:
          context: .
          file: ${{ matrix.service }}/Dockerfile
          target: production
          push: true
          tags: |
            ${{ env.IMAGE_PREFIX }}/${{ matrix.service }}:${{ github.sha }}
            ${{ env.IMAGE_PREFIX }}/${{ matrix.service }}:latest
          cache-from: type=gha,scope=${{ matrix.service }}
          cache-to: type=gha,mode=max,scope=${{ matrix.service }}

  build-frontend:
    needs: frontend-test
    if: github.ref == 'refs/heads/main' && needs.changes.outputs.frontend == 'true'
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write
    steps:
      - uses: actions/checkout@v4
      - uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - uses: docker/build-push-action@v6
        with:
          context: frontend
          file: frontend/Dockerfile
          target: production
          push: true
          tags: |
            ${{ env.IMAGE_PREFIX }}/frontend:${{ github.sha }}
            ${{ env.IMAGE_PREFIX }}/frontend:latest
          build-args: |
            VITE_API_BASE_URL=${{ vars.PRODUCTION_API_URL }}
          cache-from: type=gha,scope=frontend
          cache-to: type=gha,mode=max,scope=frontend
```

---

#### `ci-secret-management`
**Use GitHub Secrets and Variables correctly**

✅ Good: Proper secret hierarchy
```yaml
# GitHub Settings:
# Secrets (sensitive): DB_PASSWORD, JWT_SECRET, OAUTH_CLIENT_SECRET
# Variables (non-sensitive): PRODUCTION_API_URL, DEPLOY_HOST, DB_NAME

# Usage in workflow
env:
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
  API_URL: ${{ vars.PRODUCTION_API_URL }}
```

Never put secrets in:
- Docker build args (visible in image layers)
- Workflow logs (use `::add-mask::`)
- Committed .env files

---

#### `ci-pr-checks`
**Enforce quality gates on pull requests**

✅ Good: Comprehensive PR checks
```yaml
# .github/workflows/pr-check.yml
name: PR Checks

on:
  pull_request:
    branches: [main, develop]

concurrency:
  group: ${{ github.workflow }}-${{ github.head_ref }}
  cancel-in-progress: true

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Check commit messages
        uses: wagoid/commitlint-github-action@v6

      - name: Check for secrets in code
        uses: trufflesecurity/trufflehog@main
        with:
          extra_args: --only-verified
```

---

### 4. Deployment Strategies (HIGH)

#### `deploy-blue-green`
**Implement Blue-Green deployment with zero downtime**

✅ Good: Blue-Green deployment script
```bash
#!/bin/bash
# scripts/deploy-blue-green.sh

set -euo pipefail

SERVICE_NAME=$1
IMAGE_TAG=$2
DEPLOY_HOST=$3

# Determine current active (blue or green)
CURRENT=$(ssh $DEPLOY_HOST "docker ps --filter name=${SERVICE_NAME} --format '{{.Names}}'" | grep -oE '(blue|green)' || echo "none")

if [ "$CURRENT" = "blue" ]; then
  NEW_COLOR="green"
else
  NEW_COLOR="blue"
fi

echo "Current: $CURRENT → Deploying: $NEW_COLOR"

# Pull and start new version
ssh $DEPLOY_HOST << EOF
  docker pull ${IMAGE_TAG}

  docker run -d \
    --name ${SERVICE_NAME}-${NEW_COLOR} \
    --network app-network \
    --env-file /opt/app/.env.production \
    --restart unless-stopped \
    --health-cmd "wget -qO- http://localhost:8080/actuator/health || exit 1" \
    --health-interval 5s \
    --health-timeout 3s \
    --health-retries 10 \
    --health-start-period 30s \
    ${IMAGE_TAG}
EOF

# Wait for health check to pass
echo "Waiting for ${NEW_COLOR} to become healthy..."
for i in $(seq 1 60); do
  HEALTH=$(ssh $DEPLOY_HOST "docker inspect --format='{{.State.Health.Status}}' ${SERVICE_NAME}-${NEW_COLOR}" 2>/dev/null || echo "starting")
  if [ "$HEALTH" = "healthy" ]; then
    echo "✅ ${NEW_COLOR} is healthy"
    break
  fi
  if [ "$i" -eq 60 ]; then
    echo "❌ Health check timeout. Rolling back."
    ssh $DEPLOY_HOST "docker rm -f ${SERVICE_NAME}-${NEW_COLOR}"
    exit 1
  fi
  sleep 2
done

# Switch nginx upstream
ssh $DEPLOY_HOST << EOF
  sed -i "s/${SERVICE_NAME}-${CURRENT}/${SERVICE_NAME}-${NEW_COLOR}/g" /etc/nginx/conf.d/upstream.conf
  nginx -t && nginx -s reload
EOF

# Remove old container after grace period
sleep 10
if [ "$CURRENT" != "none" ]; then
  ssh $DEPLOY_HOST "docker rm -f ${SERVICE_NAME}-${CURRENT}" || true
fi

echo "✅ Deployment complete: ${SERVICE_NAME} → ${NEW_COLOR}"
```

---

#### `deploy-nginx-upstream`
**Configure nginx for multi-service routing**

✅ Good: Production nginx configuration
```nginx
# /etc/nginx/conf.d/upstream.conf

upstream integration-api {
    server integration-service-blue:8080;
}

upstream processor-api {
    server processor-service-blue:8080;
}

server {
    listen 80;
    server_name api.lostarkforrice.com;

    # Force HTTPS in production
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.lostarkforrice.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    # Integration Service API
    location /api/v1/market/ {
        proxy_pass http://integration-api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Correlation-Id $request_id;

        # Timeouts
        proxy_connect_timeout 5s;
        proxy_read_timeout 30s;
        proxy_send_timeout 10s;
    }

    # Processor Service API
    location /api/v1/analytics/ {
        proxy_pass http://processor-api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Correlation-Id $request_id;
    }

    # Frontend (SPA)
    location / {
        root /var/www/frontend;
        try_files $uri $uri/ /index.html;

        # Cache static assets
        location ~* \.(js|css|png|jpg|svg|woff2?)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=30r/s;
    location /api/ {
        limit_req zone=api burst=50 nodelay;
    }
}
```

---

#### `deploy-docker-network`
**Isolate services with Docker networks**

✅ Good: Network segmentation
```yaml
# docker-compose.prod.yml
services:
  nginx:
    networks:
      - public
      - backend

  integration-service:
    networks:
      - backend
      - database

  processor-service:
    networks:
      - backend
      - database

  postgres:
    networks:
      - database  # Not accessible from public network

  redis:
    networks:
      - database

networks:
  public:
    driver: bridge
  backend:
    driver: bridge
    internal: false
  database:
    driver: bridge
    internal: true  # No external access
```

---

### 5. Monitoring & Health Checks (MEDIUM)

#### `health-spring-actuator`
**Configure separate liveness and readiness probes**

✅ Good: Differentiated health endpoints
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when_authorized
      probes:
        enabled: true
      group:
        liveness:
          include: livenessState
        readiness:
          include: readinessState, db, redis
  health:
    redis:
      enabled: true
    db:
      enabled: true

server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

```
Liveness:  GET /actuator/health/liveness   → Is the JVM alive?
Readiness: GET /actuator/health/readiness  → Can it accept traffic? (DB + Redis ready)
```

---

#### `health-frontend-endpoint`
**Add health check endpoint for frontend nginx**

✅ Good: Simple health endpoint
```nginx
# In nginx server block
location /health {
    access_log off;
    return 200 '{"status":"UP","service":"frontend"}';
    add_header Content-Type application/json;
}
```

---

#### `health-graceful-shutdown`
**Implement graceful shutdown with connection draining**

✅ Good: Spring Boot graceful shutdown with pre-stop hook
```kotlin
// Application configuration
@Configuration
class ShutdownConfig {
    private val logger = KotlinLogging.logger {}

    @PreDestroy
    fun onShutdown() {
        logger.info { "Shutting down gracefully..." }
    }
}
```

```yaml
# Docker compose / Kubernetes
services:
  integration-service:
    stop_grace_period: 35s  # > spring.lifecycle.timeout-per-shutdown-phase
```

In Docker Compose, SIGTERM is sent first. The application has `stop_grace_period` seconds to finish in-flight requests before SIGKILL.

---

#### `health-log-aggregation`
**Configure structured JSON logging for all services**

✅ Good: Consistent structured logging across services
```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="local">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO"><appender-ref ref="CONSOLE"/></root>
    </springProfile>

    <springProfile name="prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <includeMdcKeyName>correlationId</includeMdcKeyName>
                <includeMdcKeyName>userId</includeMdcKeyName>
            </encoder>
        </appender>
        <root level="INFO"><appender-ref ref="JSON"/></root>
    </springProfile>
</configuration>
```

---

## Anti-Patterns Cheat Sheet

| Anti-Pattern | Rule | Fix |
|-------------|------|-----|
| No healthcheck in compose | `compose-healthcheck-dependencies` | Add healthcheck + condition |
| Hardcoded secrets in compose | `compose-env-file` | .env files, gitignored |
| Single-stage Dockerfile | `docker-multistage-backend` | Multi-stage with dep caching |
| Build everything in CI | `ci-complete-pipeline` | Path-based change detection |
| Secrets in build args | `ci-secret-management` | GitHub Secrets at runtime |
| Direct deployment (stop → start) | `deploy-blue-green` | Blue-Green zero downtime |
| All containers on one network | `deploy-docker-network` | Segmented networks |
| Single /health endpoint | `health-spring-actuator` | Separate liveness/readiness |
| No graceful shutdown | `health-graceful-shutdown` | SIGTERM + drain period |
| Text log files | `health-log-aggregation` | Structured JSON logging |
