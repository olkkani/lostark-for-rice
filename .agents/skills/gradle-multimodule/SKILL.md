---
name: gradle-multimodule
description: Gradle multi-module project management patterns for Kotlin Spring Boot microservices. This skill should be used when configuring module dependencies, creating convention plugins, optimizing build performance, setting up jOOQ code generation in multi-module contexts, or configuring CI/CD for selective module builds. Triggers on tasks involving build.gradle.kts, settings.gradle.kts, buildSrc, build-logic, module separation, or Gradle build optimization.
metadata:
  author: jin
  version: "1.0.0"
---

# Gradle Multi-Module Project Management

Comprehensive guide for structuring, optimizing, and maintaining Gradle multi-module Kotlin Spring Boot projects. Contains 42 rules across 6 categories, focused on clean module boundaries, build performance, and CI/CD integration.

## When to Apply

Reference these guidelines when:
- Adding new modules or restructuring existing module boundaries
- Creating or modifying `build.gradle.kts`, `settings.gradle.kts`
- Setting up convention plugins in `buildSrc` or `build-logic`
- Configuring jOOQ code generation tasks
- Optimizing Gradle build times (caching, parallelism, daemon)
- Writing GitHub Actions CI/CD for multi-module projects
- Managing inter-module dependencies and API exposure

## Rule Categories by Priority

| Priority | Category | Impact | Prefix |
|----------|----------|--------|--------|
| 1 | Module Structure & Boundaries | CRITICAL | `module-` |
| 2 | Convention Plugins | CRITICAL | `plugin-` |
| 3 | Dependency Management | HIGH | `dep-` |
| 4 | Build Performance | HIGH | `build-` |
| 5 | jOOQ Code Generation | MEDIUM-HIGH | `jooq-` |
| 6 | CI/CD Integration | MEDIUM | `ci-` |

---

## Detailed Rules

### 1. Module Structure & Boundaries (CRITICAL)

#### `module-bounded-context`
**Separate modules by bounded context, not by technical layer**

❌ Bad: Modules by layer (all domains in one module)
```
project/
├── domain/          # ALL domain logic
├── application/     # ALL use cases
├── infrastructure/  # ALL adapters
└── web/             # ALL controllers
```

✅ Good: Modules by bounded context with internal hexagonal structure
```
lostark-for-rice/
├── core/
│   ├── domain/                    # Shared domain primitives (value objects, interfaces)
│   └── common/                    # Cross-cutting utilities (logging, serialization)
├── market-data/
│   ├── market-data-domain/        # Market domain models, ports
│   ├── market-data-application/   # Market use cases
│   └── market-data-infrastructure/ # Market adapters (API clients, DB)
├── integration-service/           # External API integration orchestration
├── processor-service/             # Data processing pipelines
├── api/                           # REST API (web layer, composes use cases)
└── frontend/                      # React TypeScript frontend
```

**Why it matters**: Layer-based modules create coupling between unrelated domains. Bounded context modules enable independent deployment, testing, and team ownership.

---

#### `module-dependency-graph`
**Define clear dependency direction, enforce with Gradle constraints**

```
┌─────────────┐
│  api         │ ── depends on ──► application modules
├─────────────┤
│ integration  │ ── depends on ──► domain modules + infrastructure
│ processor    │ ── depends on ──► domain modules + infrastructure
├─────────────┤
│ application  │ ── depends on ──► domain modules only
├─────────────┤
│  domain      │ ── depends on ──► core/domain only (no framework deps)
├─────────────┤
│  core        │ ── no project dependencies
└─────────────┘
```

✅ Good: Explicit dependency declarations
```kotlin
// market-data/market-data-application/build.gradle.kts
dependencies {
    implementation(project(":core:domain"))
    implementation(project(":market-data:market-data-domain"))
    // NOT allowed: implementation(project(":market-data:market-data-infrastructure"))
}
```

---

#### `module-api-exposure`
**Expose only necessary classes between modules via api vs implementation**

❌ Bad: Leaking internal types across modules
```kotlin
// market-data-domain/build.gradle.kts
dependencies {
    api(project(":core:domain"))  // Exposes ALL core:domain types to consumers
    api(libs.jooq)               // jOOQ leaks to domain consumers!
}
```

✅ Good: Minimal API surface
```kotlin
// market-data-domain/build.gradle.kts
dependencies {
    api(project(":core:domain"))           // Intentional: consumers need core types
    implementation(libs.kotlin.coroutines) // Internal: not exposed to consumers
}

// market-data-infrastructure/build.gradle.kts
dependencies {
    implementation(project(":market-data:market-data-domain"))
    implementation(libs.jooq)           // jOOQ stays in infrastructure
    implementation(libs.spring.boot.starter.data.jpa)
}
```

**Rule of thumb**: Use `api` only when the dependency's types appear in your module's public API signatures. Use `implementation` for everything else.

---

#### `module-settings-gradle`
**Organize settings.gradle.kts with clear include structure**

✅ Good: Well-organized module includes
```kotlin
// settings.gradle.kts
rootProject.name = "lostark-for-rice"

// Core modules
include(":core:domain")
include(":core:common")

// Market Data bounded context
include(":market-data:market-data-domain")
include(":market-data:market-data-application")
include(":market-data:market-data-infrastructure")

// Service modules
include(":integration-service")
include(":processor-service")

// API module
include(":api")

// Convention plugins
includeBuild("build-logic")

// Enable type-safe project accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
```

With `TYPESAFE_PROJECT_ACCESSORS`, you can write:
```kotlin
dependencies {
    implementation(projects.core.domain)
    implementation(projects.marketData.marketDataDomain)
}
```

---

### 2. Convention Plugins (CRITICAL)

#### `plugin-convention-pattern`
**Extract shared build logic into convention plugins**

❌ Bad: Copy-pasted configuration across every module
```kotlin
// Every module's build.gradle.kts repeats this:
plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
}
kotlin {
    jvmToolchain(21)
}
tasks.withType<Test> {
    useJUnitPlatform()
}
```

✅ Good: Convention plugin in build-logic
```
build-logic/
├── build.gradle.kts
├── settings.gradle.kts
└── src/main/kotlin/
    ├── kotlin-common.gradle.kts       # Base Kotlin config
    ├── kotlin-spring.gradle.kts       # Spring Boot additions
    ├── kotlin-jooq.gradle.kts         # jOOQ code generation
    └── kotlin-testing.gradle.kts      # Test framework config
```

```kotlin
// build-logic/settings.gradle.kts
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

// build-logic/build.gradle.kts
plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.spring.boot.gradle.plugin)
}
```

```kotlin
// build-logic/src/main/kotlin/kotlin-common.gradle.kts
plugins {
    kotlin("jvm")
}

group = "com.lostarkforrice"

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xcontext-receivers")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

```kotlin
// build-logic/src/main/kotlin/kotlin-spring.gradle.kts
plugins {
    id("kotlin-common")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

// No bootJar for library modules
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}
```

```kotlin
// Consuming module - clean and minimal
// market-data/market-data-domain/build.gradle.kts
plugins {
    id("kotlin-common")
}

dependencies {
    api(projects.core.domain)
}
```

---

#### `plugin-bootjar-control`
**Only enable bootJar for deployable service modules**

❌ Bad: All modules produce bootJar (fails for library modules)
```kotlin
// library module tries to create bootJar → error: no main class
plugins {
    id("org.springframework.boot")
}
```

✅ Good: Separate convention for services vs libraries
```kotlin
// build-logic/src/main/kotlin/kotlin-spring-service.gradle.kts
plugins {
    id("kotlin-spring")
}

// Override: enable bootJar for service modules
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
}
tasks.named<Jar>("jar") {
    enabled = false
}

// Only service modules:
// integration-service/build.gradle.kts
plugins {
    id("kotlin-spring-service")
}
```

---

### 3. Dependency Management (HIGH)

#### `dep-version-catalog-structure`
**Organize version catalog by domain, not alphabetically**

✅ Good: Grouped version catalog
```toml
# gradle/libs.versions.toml
[versions]
# Core
kotlin = "2.0.21"
spring-boot = "3.4.1"
spring-dependency-management = "1.1.7"

# Database
jooq = "3.19.15"
postgresql = "42.7.4"
flyway = "10.21.0"
hikaricp = "6.2.1"

# Async
kotlinx-coroutines = "1.9.0"

# Testing
kotest = "5.9.1"
testcontainers = "1.20.4"
mockk = "1.13.13"

# Observability
kotlin-logging = "7.0.3"
micrometer = "1.14.2"

# Build plugins
detekt = "1.23.7"
ktlint-gradle = "12.1.2"

[libraries]
# Spring
spring-boot-starter-web = { module = "org.springframework.boot:spring-boot-starter-web" }
spring-boot-starter-jooq = { module = "org.springframework.boot:spring-boot-starter-jooq" }
spring-boot-starter-security = { module = "org.springframework.boot:spring-boot-starter-security" }
spring-boot-starter-actuator = { module = "org.springframework.boot:spring-boot-starter-actuator" }
spring-boot-starter-validation = { module = "org.springframework.boot:spring-boot-starter-validation" }

# Database
jooq-core = { module = "org.jooq:jooq", version.ref = "jooq" }
jooq-codegen = { module = "org.jooq:jooq-codegen", version.ref = "jooq" }
postgresql = { module = "org.postgresql:postgresql", version.ref = "postgresql" }
flyway-core = { module = "org.flywaydb:flyway-core", version.ref = "flyway" }
flyway-postgresql = { module = "org.flywaydb:flyway-database-postgresql", version.ref = "flyway" }

# Kotlin
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
kotlinx-coroutines-reactor = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-reactor", version.ref = "kotlinx-coroutines" }
kotlin-logging = { module = "io.github.oshai:kotlin-logging", version.ref = "kotlin-logging" }

# Testing
kotest-runner = { module = "io.kotest:kotest-runner-junit5", version.ref = "kotest" }
kotest-assertions = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }
kotest-property = { module = "io.kotest:kotest-property", version.ref = "kotest" }
testcontainers-postgresql = { module = "org.testcontainers:postgresql", version.ref = "testcontainers" }
testcontainers-junit = { module = "org.testcontainers:junit-jupiter", version.ref = "testcontainers" }
mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
spring-boot-starter-test = { module = "org.springframework.boot:spring-boot-starter-test" }

[bundles]
spring-web = ["spring-boot-starter-web", "spring-boot-starter-validation", "spring-boot-starter-actuator"]
database = ["spring-boot-starter-jooq", "postgresql", "flyway-core", "flyway-postgresql"]
coroutines = ["kotlinx-coroutines-core", "kotlinx-coroutines-reactor"]
testing = ["kotest-runner", "kotest-assertions", "mockk", "spring-boot-starter-test"]
testing-integration = ["testcontainers-postgresql", "testcontainers-junit"]

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
kotlin-spring = { id = "org.jetbrains.kotlin.plugin.spring", version.ref = "kotlin" }
spring-boot = { id = "org.springframework.boot", version.ref = "spring-boot" }
spring-dependency-management = { id = "io.spring.dependency-management", version.ref = "spring-dependency-management" }
jooq = { id = "nu.studer.jooq", version = "9.0" }
detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }
ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref = "ktlint-gradle" }
```

---

#### `dep-platform-bom`
**Use Spring Boot BOM for managed dependency versions**

✅ Good: Let Spring manage transitive versions
```kotlin
// build-logic/src/main/kotlin/kotlin-spring.gradle.kts
plugins {
    id("kotlin-common")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

// Spring Boot BOM manages versions for:
// - Jackson, Netty, Micrometer, Logback, etc.
// Don't override these unless absolutely necessary.
```

```kotlin
// module build.gradle.kts - no version needed for managed deps
dependencies {
    implementation(libs.spring.boot.starter.web)  // version from BOM
    implementation(libs.jooq.core)                // explicit version in catalog
}
```

---

#### `dep-no-unnecessary-transitive`
**Prevent unintended transitive dependency exposure**

✅ Good: Use `implementation` by default, document `api` usage
```kotlin
// core/domain/build.gradle.kts
dependencies {
    // api: exposed because domain model types extend/use these
    api(libs.kotlinx.coroutines.core)

    // implementation: used internally, not part of public API
    implementation(libs.kotlin.logging)
}
```

---

### 4. Build Performance (HIGH)

#### `build-parallel-execution`
**Enable parallel execution and build caching**

✅ Good: Optimized gradle.properties
```properties
# gradle.properties

# Parallel execution
org.gradle.parallel=true
org.gradle.workers.max=4

# Build cache
org.gradle.caching=true

# Daemon
org.gradle.daemon=true
org.gradle.daemon.idletimeout=10800000

# JVM memory
org.gradle.jvmargs=-Xmx4g -XX:+UseParallelGC -XX:MaxMetaspaceSize=512m

# Kotlin incremental compilation
kotlin.incremental=true
kotlin.incremental.usePreciseJavaTracking=true

# Configuration cache (Gradle 8.1+)
org.gradle.configuration-cache=true
org.gradle.configuration-cache.problems=warn
```

---

#### `build-task-avoidance`
**Use task avoidance API to prevent unnecessary configuration**

❌ Bad: Eager task creation
```kotlin
tasks.create("generateDocs") {  // Configures even if never executed
    doLast { /* ... */ }
}
```

✅ Good: Lazy task registration
```kotlin
tasks.register("generateDocs") {  // Only configured when needed
    doLast { /* ... */ }
}
```

---

#### `build-selective-testing`
**Test only affected modules in CI**

✅ Good: Gradle's built-in change detection
```bash
# CI script - build only changed modules
./gradlew build --build-cache

# Or explicitly test specific module
./gradlew :market-data:market-data-application:test

# Run all tests but skip up-to-date modules (with build cache)
./gradlew test --build-cache --parallel
```

---

#### `build-dependency-locking`
**Use dependency locking for reproducible builds**

✅ Good: Lock file for critical dependencies
```kotlin
// build-logic/src/main/kotlin/kotlin-common.gradle.kts
dependencyLocking {
    lockAllConfigurations()
}
```

```bash
# Generate lock files
./gradlew dependencies --write-locks

# Verify in CI
./gradlew dependencies --verify-locks
```

---

### 5. jOOQ Code Generation (MEDIUM-HIGH)

#### `jooq-codegen-module-isolation`
**Isolate jOOQ code generation in infrastructure module**

✅ Good: jOOQ codegen only in infrastructure
```
market-data/
├── market-data-domain/          # No jOOQ dependency
├── market-data-application/     # No jOOQ dependency
└── market-data-infrastructure/
    ├── build.gradle.kts         # jOOQ plugin + codegen config
    └── src/
        ├── main/kotlin/
        │   └── adapter/outbound/persistence/
        │       ├── JooqMarketRepository.kt
        │       └── mapper/                    # Record ↔ Domain mappers
        └── generated/                         # jOOQ generated classes
            └── jooq/
```

---

#### `jooq-codegen-flyway-integration`
**Generate jOOQ classes from Flyway-migrated schema**

✅ Good: Testcontainers-based jOOQ codegen
```kotlin
// market-data-infrastructure/build.gradle.kts
plugins {
    id("kotlin-spring")
    id("nu.studer.jooq")
}

jooq {
    version.set(libs.versions.jooq)
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5432/lostark"
                    user = "dev"
                    password = "dev"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = "flyway_schema_history"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isKotlinNotNullPojoAttributes = true
                        isKotlinNotNullRecordAttributes = true
                    }
                    target.apply {
                        packageName = "com.lostarkforrice.marketdata.infrastructure.jooq"
                        directory = "src/generated/jooq"
                    }
                }
            }
        }
    }
}

// Add generated sources to source sets
sourceSets {
    main {
        kotlin {
            srcDir("src/generated/jooq")
        }
    }
}
```

**Alternative: Flyway + Testcontainers based codegen (CI-friendly)**
```kotlin
// For CI where no running DB is available, use testcontainers-based codegen
// See: https://www.jooq.org/doc/latest/manual/code-generation/codegen-gradle/
// Use the org.jooq.meta.extensions.ddl.DDLDatabase to generate from SQL files
generator.apply {
    database.apply {
        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
        properties.add(
            Property().apply {
                key = "scripts"
                value = "src/main/resources/db/migration/*.sql"
            }
        )
    }
}
```

---

#### `jooq-record-domain-mapper`
**Separate jOOQ records from domain entity mapping**

❌ Bad: jOOQ records used directly as domain entities
```kotlin
fun findById(id: Long): MarketItemRecord? =  // Leaks jOOQ type
    dsl.selectFrom(MARKET_ITEM).where(MARKET_ITEM.ID.eq(id)).fetchOne()
```

✅ Good: Explicit mapper between layers
```kotlin
// infrastructure/adapter/outbound/persistence/mapper/MarketItemMapper.kt
object MarketItemMapper {
    fun MarketItemRecord.toDomain(): MarketItem = MarketItem(
        id = MarketItemId(this.id),
        name = this.name,
        category = ItemCategory.valueOf(this.category),
        currentPrice = Money(this.currentPrice),
        updatedAt = this.updatedAt
    )

    fun MarketItem.toRecord(): MarketItemRecord = MarketItemRecord().apply {
        this.id = this@toRecord.id.value
        this.name = this@toRecord.name
        this.category = this@toRecord.category.name
        this.currentPrice = this@toRecord.currentPrice.value
    }
}

// Repository implementation
@Repository
class JooqMarketItemRepository(
    private val dsl: DSLContext
) : MarketItemRepository {
    override suspend fun findById(id: MarketItemId): MarketItem? =
        dsl.selectFrom(MARKET_ITEM)
            .where(MARKET_ITEM.ID.eq(id.value))
            .fetchOne()
            ?.toDomain()
}
```

---

### 6. CI/CD Integration (MEDIUM)

#### `ci-change-detection`
**Build and deploy only changed modules in GitHub Actions**

✅ Good: Path-based change detection
```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  detect-changes:
    runs-on: ubuntu-latest
    outputs:
      backend: ${{ steps.changes.outputs.backend }}
      frontend: ${{ steps.changes.outputs.frontend }}
      market-data: ${{ steps.changes.outputs.market-data }}
      integration: ${{ steps.changes.outputs.integration }}
      processor: ${{ steps.changes.outputs.processor }}
    steps:
      - uses: actions/checkout@v4
      - uses: dorny/paths-filter@v3
        id: changes
        with:
          filters: |
            backend:
              - 'core/**'
              - 'build-logic/**'
              - 'gradle/**'
              - 'build.gradle.kts'
              - 'settings.gradle.kts'
            market-data:
              - 'market-data/**'
              - 'core/**'
            integration:
              - 'integration-service/**'
              - 'core/**'
            processor:
              - 'processor-service/**'
              - 'core/**'
            frontend:
              - 'frontend/**'

  backend-test:
    needs: detect-changes
    if: needs.detect-changes.outputs.backend == 'true' || needs.detect-changes.outputs.market-data == 'true'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
      - uses: gradle/actions/setup-gradle@v4
        with:
          cache-read-only: ${{ github.ref != 'refs/heads/main' }}
      - run: ./gradlew test --build-cache --parallel

  frontend-test:
    needs: detect-changes
    if: needs.detect-changes.outputs.frontend == 'true'
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: pnpm
          cache-dependency-path: frontend/pnpm-lock.yaml
      - run: pnpm install --frozen-lockfile
      - run: pnpm run type-check
      - run: pnpm run lint
      - run: pnpm run test
```

---

#### `ci-gradle-cache`
**Optimize Gradle caching in GitHub Actions**

✅ Good: Gradle cache with proper configuration
```yaml
# Shared setup step
- uses: gradle/actions/setup-gradle@v4
  with:
    cache-read-only: ${{ github.ref != 'refs/heads/main' }}
    gradle-home-cache-includes: |
      caches
      notifications
      jdks
    gradle-home-cache-excludes: |
      caches/journal-1
      caches/*/executionHistory
```

---

#### `ci-docker-build-selective`
**Build Docker images only for changed services**

✅ Good: Selective Docker build and push
```yaml
  deploy-integration:
    needs: [detect-changes, backend-test]
    if: |
      github.ref == 'refs/heads/main' &&
      (needs.detect-changes.outputs.integration == 'true' || needs.detect-changes.outputs.backend == 'true')
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
      - uses: gradle/actions/setup-gradle@v4

      - name: Build integration-service
        run: ./gradlew :integration-service:bootJar --build-cache

      - name: Build and push Docker image
        uses: docker/build-push-action@v6
        with:
          context: integration-service
          push: true
          tags: ghcr.io/${{ github.repository }}/integration-service:${{ github.sha }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

---

#### `ci-matrix-strategy`
**Use matrix strategy for parallel module testing**

✅ Good: Matrix-based parallel testing
```yaml
  module-test:
    needs: detect-changes
    strategy:
      fail-fast: false
      matrix:
        include:
          - module: market-data
            changed: ${{ needs.detect-changes.outputs.market-data }}
            gradle-task: ":market-data:market-data-application:test :market-data:market-data-infrastructure:test"
          - module: integration
            changed: ${{ needs.detect-changes.outputs.integration }}
            gradle-task: ":integration-service:test"
          - module: processor
            changed: ${{ needs.detect-changes.outputs.processor }}
            gradle-task: ":processor-service:test"
    runs-on: ubuntu-latest
    if: matrix.changed == 'true'
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
      - uses: gradle/actions/setup-gradle@v4
      - run: ./gradlew ${{ matrix.gradle-task }} --build-cache
```

---

## Anti-Patterns Cheat Sheet

| Anti-Pattern | Rule | Fix |
|-------------|------|-----|
| Copy-paste build config | `plugin-convention-pattern` | Convention plugins in build-logic |
| All modules produce bootJar | `plugin-bootjar-control` | Enable only for service modules |
| Alphabetical version catalog | `dep-version-catalog-structure` | Group by domain |
| `api()` everywhere | `dep-no-unnecessary-transitive` | Default to `implementation()` |
| Slow CI builds everything | `ci-change-detection` | Path-based change detection |
| jOOQ types in domain layer | `jooq-record-domain-mapper` | Explicit mapper in infrastructure |
| jOOQ codegen in domain module | `jooq-codegen-module-isolation` | Infrastructure module only |
| No build caching | `build-parallel-execution` | Enable cache + parallel |
| Eager task creation | `build-task-avoidance` | `tasks.register` instead of `tasks.create` |
| Layer-based modules | `module-bounded-context` | Bounded context modules |
