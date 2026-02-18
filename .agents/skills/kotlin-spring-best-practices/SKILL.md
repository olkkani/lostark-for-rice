---
name: kotlin-spring-best-practices
description: Kotlin and Spring Boot backend performance and architecture optimization guidelines. This skill should be used when writing, reviewing, or refactoring Kotlin/Spring Boot backend code to ensure optimal performance patterns, clean architecture, and production-ready quality. Triggers on tasks involving Spring Boot services, Kotlin coroutines, database access (jOOQ/JPA), hexagonal architecture, API design, or backend performance improvements.
license: MIT
metadata:
  author: jin
  version: "1.0.0"
---

# Kotlin & Spring Boot Backend Best Practices

Comprehensive performance and architecture optimization guide for Kotlin and Spring Boot backend applications. Contains 72 rules across 9 categories, prioritized by impact to guide automated refactoring and code generation.

## When to Apply

Reference these guidelines when:
- Writing new Spring Boot services or Kotlin modules
- Implementing hexagonal architecture (ports & adapters)
- Working with database access (jOOQ, JPA, R2DBC)
- Reviewing backend code for performance or architecture issues
- Refactoring existing Kotlin/Spring Boot code
- Designing APIs (REST, gRPC)
- Implementing coroutine-based async patterns
- Setting up CI/CD, Docker, or cloud deployment

## Rule Categories by Priority

| Priority | Category | Impact | Prefix |
|----------|----------|--------|--------|
| 1 | Architecture & Module Design | CRITICAL | `arch-` |
| 2 | Database & Query Optimization | CRITICAL | `db-` |
| 3 | Coroutines & Async Patterns | HIGH | `async-` |
| 4 | Spring Boot Configuration | HIGH | `spring-` |
| 5 | API Design & Validation | MEDIUM-HIGH | `api-` |
| 6 | Error Handling & Resilience | MEDIUM-HIGH | `error-` |
| 7 | Testing Strategies | MEDIUM | `test-` |
| 8 | Kotlin Idioms & Performance | MEDIUM | `kotlin-` |
| 9 | Deployment & Observability | LOW-MEDIUM | `deploy-` |

## Quick Reference

### 1. Architecture & Module Design (CRITICAL)

- `arch-hexagonal-structure` - Enforce hexagonal package structure: domain, application, infrastructure
- `arch-dependency-direction` - Dependencies point inward: infra → application → domain
- `arch-port-interface` - Define ports as interfaces in domain/application layer
- `arch-adapter-impl` - Implement adapters in infrastructure layer only
- `arch-domain-no-framework` - Keep domain layer free of Spring/framework annotations
- `arch-usecase-single-responsibility` - One use case per class, single public execute method
- `arch-multi-module-gradle` - Separate modules by bounded context, share via API module
- `arch-version-catalog` - Use Gradle version catalogs (libs.versions.toml) for dependency management

### 2. Database & Query Optimization (CRITICAL)

- `db-n-plus-one` - Detect and eliminate N+1 queries with eager fetching or batch loading
- `db-jooq-typesafe` - Use jOOQ generated classes for type-safe queries, avoid raw SQL strings
- `db-index-strategy` - Create indexes based on query patterns, not table structure
- `db-connection-pool` - Configure HikariCP pool size: connections = (cores * 2) + disk_spindles
- `db-batch-operations` - Use batch inserts/updates for bulk operations
- `db-pagination-keyset` - Use keyset pagination instead of OFFSET for large datasets
- `db-transaction-scope` - Keep transactions as short as possible, no external calls inside
- `db-read-replica` - Route read queries to replicas when available
- `db-migration-flyway` - Use Flyway for versioned, repeatable migrations
- `db-jooq-dao-pattern` - Separate jOOQ record mapping from domain entity conversion

### 3. Coroutines & Async Patterns (HIGH)

- `async-structured-concurrency` - Always use structured concurrency with coroutineScope
- `async-dispatcher-io` - Use Dispatchers.IO for blocking I/O, never block main dispatcher
- `async-parallel-decomposition` - Use async {} + awaitAll() for independent operations
- `async-flow-backpressure` - Use Flow with buffer/conflate for backpressure handling
- `async-cancellation-cooperative` - Check isActive and use ensureActive() for cancellation support
- `async-supervisor-scope` - Use supervisorScope when child failure shouldn't cancel siblings
- `async-webflux-coroutine-bridge` - Bridge WebFlux Mono/Flux to suspend functions properly
- `async-avoid-globalscope` - Never use GlobalScope, inject CoroutineScope via DI

### 4. Spring Boot Configuration (HIGH)

- `spring-config-properties` - Use @ConfigurationProperties with data classes, not @Value
- `spring-profile-separation` - Separate configs per profile: application-{profile}.yml
- `spring-bean-conditional` - Use @ConditionalOnProperty for feature toggles
- `spring-startup-optimization` - Use lazy initialization for non-critical beans
- `spring-actuator-security` - Expose only necessary actuator endpoints, secure the rest
- `spring-jackson-config` - Configure Jackson globally: snake_case, ignore unknown, ISO dates
- `spring-constructor-injection` - Always use constructor injection, never field injection
- `spring-component-scan-explicit` - Limit component scan to specific packages

### 5. API Design & Validation (MEDIUM-HIGH)

- `api-dto-separation` - Separate request/response DTOs from domain entities
- `api-validation-jakarta` - Use Jakarta validation annotations on DTOs, not domain
- `api-error-response-standard` - Use RFC 7807 Problem Details for error responses
- `api-versioning-header` - Version APIs via header or path prefix consistently
- `api-pagination-standard` - Return standardized page metadata with cursor support
- `api-idempotency-key` - Support idempotency keys for non-idempotent operations
- `api-rate-limiting` - Implement rate limiting per client/endpoint
- `api-openapi-spec` - Generate OpenAPI spec from code, not manually

### 6. Error Handling & Resilience (MEDIUM-HIGH)

- `error-sealed-class` - Use Kotlin sealed classes for domain error modeling
- `error-result-type` - Return Result<T> or custom Either type instead of throwing in domain
- `error-global-handler` - Use @RestControllerAdvice for centralized exception mapping
- `error-retry-exponential` - Implement exponential backoff for transient failures
- `error-circuit-breaker` - Use circuit breaker for external service calls
- `error-timeout-all-external` - Set timeouts on ALL external calls (HTTP, DB, messaging)
- `error-dead-letter-queue` - Route failed async messages to dead letter queue
- `error-correlation-id` - Propagate correlation IDs across service boundaries

### 7. Testing Strategies (MEDIUM)

- `test-architecture-fitness` - Use ArchUnit to enforce architecture rules automatically
- `test-testcontainers-integration` - Use Testcontainers for real DB/Redis in integration tests
- `test-slice-tests` - Use @WebMvcTest, @DataJpaTest for focused slice testing
- `test-kotest-style` - Use Kotest with BehaviorSpec or FunSpec for readable tests
- `test-fixture-factory` - Create test fixture factories, avoid hardcoded test data
- `test-mock-external-only` - Mock only external boundaries, not internal collaborators
- `test-contract-testing` - Use Spring Cloud Contract for API contract verification
- `test-no-spring-in-unit` - Unit tests should never load Spring context

### 8. Kotlin Idioms & Performance (MEDIUM)

- `kotlin-data-class-domain` - Use data classes for value objects, regular classes for entities
- `kotlin-extension-functions` - Use extension functions for cross-cutting utility, not core logic
- `kotlin-sealed-interface` - Prefer sealed interfaces over sealed classes for flexibility
- `kotlin-sequence-large-collections` - Use sequences for large collection pipelines (lazy eval)
- `kotlin-inline-value-class` - Use @JvmInline value class for type-safe wrappers (ID types, etc.)
- `kotlin-scope-functions` - Use apply for configuration, let for null-safe transforms, run for scoping
- `kotlin-avoid-it-nesting` - Name lambda parameters when nesting higher-order functions
- `kotlin-coroutine-context-elements` - Use CoroutineContext elements for cross-cutting concerns

### 9. Deployment & Observability (LOW-MEDIUM)

- `deploy-docker-multi-stage` - Use multi-stage Docker builds with distroless/JRE-slim base
- `deploy-jvm-flags` - Configure JVM flags for containers: -XX:+UseContainerSupport, memory limits
- `deploy-health-checks` - Implement liveness and readiness probes separately
- `deploy-graceful-shutdown` - Configure graceful shutdown with drain period for in-flight requests
- `deploy-structured-logging` - Use structured JSON logging with MDC for correlation
- `deploy-metrics-micrometer` - Export business metrics via Micrometer, not just JVM stats
- `deploy-secret-management` - Never hardcode secrets, use vault or environment injection
- `deploy-ghcr-ci` - Use GitHub Container Registry with GitHub Actions for CI/CD pipeline

---

## Detailed Rules

### 1. Architecture & Module Design (CRITICAL)

#### `arch-hexagonal-structure`
**Enforce hexagonal package structure: domain, application, infrastructure**

❌ Bad: Flat or layer-by-type structure
```
com.example.service/
├── controllers/
├── services/
├── repositories/
├── models/
└── dtos/
```

✅ Good: Hexagonal architecture with clear boundaries
```
com.example.auction/
├── domain/
│   ├── model/          # Entities, Value Objects
│   ├── port/
│   │   ├── inbound/    # Use case interfaces
│   │   └── outbound/   # Repository & external service interfaces
│   └── service/        # Domain services (pure business logic)
├── application/
│   ├── usecase/        # Use case implementations
│   └── port/           # Application-level port interfaces (if needed)
└── infrastructure/
    ├── adapter/
    │   ├── inbound/
    │   │   ├── web/    # REST controllers
    │   │   └── messaging/  # Event consumers
    │   └── outbound/
    │       ├── persistence/ # jOOQ/JPA implementations
    │       └── external/    # HTTP clients, SDKs
    └── config/         # Spring configurations
```

**Why it matters**: Clear dependency boundaries prevent accidental coupling. Domain layer stays testable without framework dependencies. Infrastructure can be swapped without touching business logic.

---

#### `arch-dependency-direction`
**Dependencies point inward: infra → application → domain**

❌ Bad: Domain depends on infrastructure
```kotlin
// domain/model/Auction.kt
import org.springframework.data.annotation.Id  // Framework leak!
import com.example.infrastructure.persistence.AuctionRecord  // Outward dependency!

data class Auction(
    @Id val id: Long,
    val title: String
)
```

✅ Good: Domain is framework-free, infrastructure adapts
```kotlin
// domain/model/Auction.kt
data class Auction(
    val id: AuctionId,
    val title: String,
    val status: AuctionStatus
)

// domain/port/outbound/AuctionRepository.kt
interface AuctionRepository {
    suspend fun findById(id: AuctionId): Auction?
    suspend fun save(auction: Auction): Auction
}

// infrastructure/adapter/outbound/persistence/JooqAuctionRepository.kt
@Repository
class JooqAuctionRepository(
    private val dsl: DSLContext
) : AuctionRepository {
    override suspend fun findById(id: AuctionId): Auction? =
        dsl.selectFrom(AUCTION)
            .where(AUCTION.ID.eq(id.value))
            .fetchOneInto(Auction::class.java)
}
```

---

#### `arch-usecase-single-responsibility`
**One use case per class, single public execute method**

❌ Bad: God service with many responsibilities
```kotlin
@Service
class AuctionService(
    private val repo: AuctionRepository,
    private val notifier: NotificationService,
    private val paymentService: PaymentService
) {
    fun createAuction(...) { ... }
    fun placeBid(...) { ... }
    fun closeAuction(...) { ... }
    fun refundBid(...) { ... }
}
```

✅ Good: Focused use cases
```kotlin
class PlaceBidUseCase(
    private val auctionRepository: AuctionRepository,
    private val bidRepository: BidRepository,
    private val eventPublisher: DomainEventPublisher
) {
    suspend fun execute(command: PlaceBidCommand): Result<Bid> {
        val auction = auctionRepository.findById(command.auctionId)
            ?: return Result.failure(AuctionNotFound(command.auctionId))

        return auction.placeBid(command.bidderId, command.amount)
            .onSuccess { bid ->
                bidRepository.save(bid)
                eventPublisher.publish(BidPlaced(bid))
            }
    }
}
```

---

#### `arch-version-catalog`
**Use Gradle version catalogs (libs.versions.toml) for dependency management**

❌ Bad: Scattered version declarations
```kotlin
// build.gradle.kts
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.0")
    implementation("org.jooq:jooq:3.19.6")
    implementation("io.github.oshai:kotlin-logging:7.0.0")
}
```

✅ Good: Centralized version catalog
```toml
# gradle/libs.versions.toml
[versions]
spring-boot = "3.3.0"
jooq = "3.19.6"
kotlin-logging = "7.0.0"
kotest = "5.9.0"
testcontainers = "1.19.8"

[libraries]
spring-boot-starter-web = { module = "org.springframework.boot:spring-boot-starter-web", version.ref = "spring-boot" }
jooq = { module = "org.jooq:jooq", version.ref = "jooq" }
kotlin-logging = { module = "io.github.oshai:kotlin-logging", version.ref = "kotlin-logging" }

[bundles]
testing = ["kotest-runner", "kotest-assertions", "testcontainers-postgresql"]
```

```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.jooq)
    implementation(libs.kotlin.logging)
    testImplementation(libs.bundles.testing)
}
```

---

### 2. Database & Query Optimization (CRITICAL)

#### `db-n-plus-one`
**Detect and eliminate N+1 queries with eager fetching or batch loading**

❌ Bad: N+1 query in loop
```kotlin
fun getAuctionsWithBids(): List<AuctionWithBids> {
    val auctions = dsl.selectFrom(AUCTION).fetch()
    return auctions.map { auction ->
        // This fires a query PER auction!
        val bids = dsl.selectFrom(BID)
            .where(BID.AUCTION_ID.eq(auction.id))
            .fetch()
        AuctionWithBids(auction.into(Auction::class.java), bids)
    }
}
```

✅ Good: Single join query or batch fetch
```kotlin
fun getAuctionsWithBids(): List<AuctionWithBids> {
    return dsl.select()
        .from(AUCTION)
        .leftJoin(BID).on(BID.AUCTION_ID.eq(AUCTION.ID))
        .fetch()
        .intoGroups(AUCTION)
        .map { (auction, records) ->
            AuctionWithBids(
                auction = auction.into(Auction::class.java),
                bids = records.into(Bid::class.java)
            )
        }
}
```

---

#### `db-pagination-keyset`
**Use keyset pagination instead of OFFSET for large datasets**

❌ Bad: OFFSET pagination degrades at scale
```kotlin
fun getAuctions(page: Int, size: Int): List<Auction> =
    dsl.selectFrom(AUCTION)
        .orderBy(AUCTION.CREATED_AT.desc())
        .limit(size)
        .offset(page * size)  // Scans and discards rows!
        .fetchInto(Auction::class.java)
```

✅ Good: Keyset (cursor) pagination
```kotlin
fun getAuctions(cursor: Instant?, size: Int): CursorPage<Auction> {
    val condition = cursor?.let { AUCTION.CREATED_AT.lt(it) } ?: DSL.trueCondition()

    val results = dsl.selectFrom(AUCTION)
        .where(condition)
        .orderBy(AUCTION.CREATED_AT.desc())
        .limit(size + 1)  // Fetch one extra to detect hasNext
        .fetchInto(Auction::class.java)

    val hasNext = results.size > size
    val items = if (hasNext) results.dropLast(1) else results
    val nextCursor = items.lastOrNull()?.createdAt

    return CursorPage(items, nextCursor, hasNext)
}
```

---

#### `db-transaction-scope`
**Keep transactions as short as possible, no external calls inside**

❌ Bad: External HTTP call inside transaction
```kotlin
@Transactional
suspend fun processPayment(orderId: Long) {
    val order = orderRepository.findById(orderId)
    val result = paymentGateway.charge(order.amount)  // HTTP call in TX!
    order.markPaid(result.transactionId)
    orderRepository.save(order)
}
```

✅ Good: External call outside transaction
```kotlin
suspend fun processPayment(orderId: Long) {
    val order = orderRepository.findById(orderId)
    val result = paymentGateway.charge(order.amount)  // Outside TX

    transactionTemplate.execute {
        order.markPaid(result.transactionId)
        orderRepository.save(order)
    }
}
```

---

### 3. Coroutines & Async Patterns (HIGH)

#### `async-parallel-decomposition`
**Use async {} + awaitAll() for independent operations**

❌ Bad: Sequential execution of independent operations
```kotlin
suspend fun getAuctionDetail(id: AuctionId): AuctionDetail {
    val auction = auctionRepository.findById(id)    // waits
    val bids = bidRepository.findByAuctionId(id)    // then waits
    val seller = userRepository.findById(auction.sellerId) // then waits
    return AuctionDetail(auction, bids, seller)
}
```

✅ Good: Parallel execution
```kotlin
suspend fun getAuctionDetail(id: AuctionId): AuctionDetail = coroutineScope {
    val auctionDeferred = async { auctionRepository.findById(id) }
    val bidsDeferred = async { bidRepository.findByAuctionId(id) }

    val auction = auctionDeferred.await()
    val seller = async { userRepository.findById(auction.sellerId) }

    AuctionDetail(auction, bidsDeferred.await(), seller.await())
}
```

---

#### `async-avoid-globalscope`
**Never use GlobalScope, inject CoroutineScope via DI**

❌ Bad: Fire-and-forget with GlobalScope
```kotlin
@Service
class NotificationService {
    fun sendAsync(notification: Notification) {
        GlobalScope.launch {  // Leaked coroutine, no lifecycle management
            sendEmail(notification)
        }
    }
}
```

✅ Good: Inject scope tied to application lifecycle
```kotlin
@Configuration
class CoroutineConfig {
    @Bean
    fun applicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("app"))
}

@Service
class NotificationService(
    private val applicationScope: CoroutineScope
) {
    fun sendAsync(notification: Notification) {
        applicationScope.launch {
            sendEmail(notification)
        }
    }
}
```

---

### 4. Spring Boot Configuration (HIGH)

#### `spring-config-properties`
**Use @ConfigurationProperties with data classes, not @Value**

❌ Bad: Scattered @Value annotations
```kotlin
@Service
class StorageService(
    @Value("\${storage.bucket}") private val bucket: String,
    @Value("\${storage.region}") private val region: String,
    @Value("\${storage.max-size:10485760}") private val maxSize: Long
)
```

✅ Good: Type-safe configuration class
```kotlin
@ConfigurationProperties(prefix = "storage")
data class StorageProperties(
    val bucket: String,
    val region: String,
    val maxSize: Long = 10_485_760
)

@Service
class StorageService(private val props: StorageProperties) {
    fun upload(file: ByteArray) {
        require(file.size <= props.maxSize) { "File exceeds ${props.maxSize} bytes" }
        // ...
    }
}
```

---

#### `spring-constructor-injection`
**Always use constructor injection, never field injection**

❌ Bad: Field injection hides dependencies, breaks testability
```kotlin
@Service
class AuctionService {
    @Autowired
    private lateinit var auctionRepository: AuctionRepository

    @Autowired
    private lateinit var eventPublisher: EventPublisher
}
```

✅ Good: Constructor injection (Kotlin primary constructor)
```kotlin
@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val eventPublisher: EventPublisher
)
```

---

### 5. API Design & Validation (MEDIUM-HIGH)

#### `api-error-response-standard`
**Use RFC 7807 Problem Details for error responses**

❌ Bad: Inconsistent error format
```kotlin
// Sometimes this...
{ "error": "not found" }
// Sometimes this...
{ "code": 400, "message": "Invalid input", "details": [...] }
```

✅ Good: RFC 7807 Problem Details
```kotlin
data class ProblemDetail(
    val type: URI = URI("about:blank"),
    val title: String,
    val status: Int,
    val detail: String? = null,
    val instance: URI? = null,
    val extensions: Map<String, Any> = emptyMap()
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuctionNotFound::class)
    fun handleNotFound(ex: AuctionNotFound) = ProblemDetail(
        type = URI("/problems/auction-not-found"),
        title = "Auction Not Found",
        status = 404,
        detail = "Auction ${ex.auctionId} does not exist"
    ).let { ResponseEntity.status(404).body(it) }
}
```

---

### 6. Error Handling & Resilience (MEDIUM-HIGH)

#### `error-sealed-class`
**Use Kotlin sealed classes for domain error modeling**

❌ Bad: Throwing exceptions for expected business cases
```kotlin
fun placeBid(amount: Money): Bid {
    if (status != AuctionStatus.ACTIVE) throw IllegalStateException("Auction not active")
    if (amount <= currentBid) throw IllegalArgumentException("Bid too low")
    // ...
}
```

✅ Good: Sealed class for explicit error modeling
```kotlin
sealed class BidError {
    data class AuctionNotActive(val auctionId: AuctionId) : BidError()
    data class BidTooLow(val minimum: Money, val offered: Money) : BidError()
    data class BidderBlocked(val reason: String) : BidError()
}

fun placeBid(amount: Money): Either<BidError, Bid> {
    if (status != AuctionStatus.ACTIVE)
        return Either.Left(BidError.AuctionNotActive(id))
    if (amount <= currentBid)
        return Either.Left(BidError.BidTooLow(minimum = currentBid, offered = amount))

    return Either.Right(Bid(auctionId = id, amount = amount))
}
```

---

### 7. Testing Strategies (MEDIUM)

#### `test-architecture-fitness`
**Use ArchUnit to enforce architecture rules automatically**

```kotlin
@AnalyzeClasses(packages = ["com.example.auction"])
class ArchitectureTest {

    @ArchTest
    val domainShouldNotDependOnInfrastructure: ArchRule =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")

    @ArchTest
    val useCasesShouldNotDependOnWeb: ArchRule =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.inbound.web..")

    @ArchTest
    val controllersShouldNotAccessRepositoriesDirectly: ArchRule =
        noClasses().that().haveSimpleNameEndingWith("Controller")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
}
```

---

#### `test-testcontainers-integration`
**Use Testcontainers for real DB/Redis in integration tests**

```kotlin
@SpringBootTest
@Testcontainers
class JooqAuctionRepositoryTest {

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @Autowired
    lateinit var repository: AuctionRepository

    @Test
    fun `should save and retrieve auction`() {
        val auction = Auction(title = "Test Auction", status = AuctionStatus.DRAFT)
        val saved = repository.save(auction)
        val found = repository.findById(saved.id)
        assertThat(found).isEqualTo(saved)
    }
}
```

---

### 8. Kotlin Idioms & Performance (MEDIUM)

#### `kotlin-inline-value-class`
**Use @JvmInline value class for type-safe wrappers**

❌ Bad: Primitive obsession
```kotlin
fun findAuction(auctionId: Long, userId: Long): Auction?  // Easy to swap arguments!
```

✅ Good: Value class wrappers with zero runtime overhead
```kotlin
@JvmInline
value class AuctionId(val value: Long)

@JvmInline
value class UserId(val value: Long)

fun findAuction(auctionId: AuctionId, userId: UserId): Auction?  // Compile-time safety
```

---

#### `kotlin-sequence-large-collections`
**Use sequences for large collection pipelines (lazy evaluation)**

❌ Bad: Intermediate collections for large datasets
```kotlin
fun findExpensiveActiveAuctions(auctions: List<Auction>): List<String> =
    auctions
        .filter { it.status == ACTIVE }      // Creates intermediate list
        .map { it.calculateFinalPrice() }    // Creates intermediate list
        .filter { it > Money(1000) }         // Creates intermediate list
        .map { it.format() }
```

✅ Good: Sequence for lazy pipeline
```kotlin
fun findExpensiveActiveAuctions(auctions: List<Auction>): List<String> =
    auctions.asSequence()
        .filter { it.status == ACTIVE }
        .map { it.calculateFinalPrice() }
        .filter { it > Money(1000) }
        .map { it.format() }
        .toList()  // Terminal operation materializes the result
```

---

### 9. Deployment & Observability (LOW-MEDIUM)

#### `deploy-docker-multi-stage`
**Use multi-stage Docker builds with distroless/JRE-slim base**

❌ Bad: Full JDK image with source code
```dockerfile
FROM eclipse-temurin:21-jdk
COPY . /app
WORKDIR /app
RUN ./gradlew bootJar
CMD ["java", "-jar", "build/libs/app.jar"]
```

✅ Good: Multi-stage with minimal runtime image
```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts gradle/libs.versions.toml ./
RUN ./gradlew dependencies --no-daemon
COPY src/ src/
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
```

---

#### `deploy-structured-logging`
**Use structured JSON logging with MDC for correlation**

```kotlin
// logback-spring.xml - JSON structured output
// Application code
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

@Component
class CorrelationIdFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val correlationId = exchange.request.headers
            .getFirst("X-Correlation-Id") ?: UUID.randomUUID().toString()
        MDC.put("correlationId", correlationId)
        return chain.filter(exchange).doFinally { MDC.clear() }
    }
}

// In use case
class PlaceBidUseCase(...) {
    suspend fun execute(command: PlaceBidCommand): Result<Bid> {
        logger.info { "Placing bid on auction=${command.auctionId} amount=${command.amount}" }
        // ...
    }
}
```

---

#### `deploy-graceful-shutdown`
**Configure graceful shutdown with drain period**

```yaml
# application.yml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

```kotlin
@Bean
fun gracefulShutdown(): GracefulShutdownCallback =
    GracefulShutdownCallback { result ->
        logger.info { "Graceful shutdown completed: $result" }
    }
```

---

## How to Use

Read individual rule files for detailed explanations and code examples:

```
rules/arch-hexagonal-structure.md
rules/db-n-plus-one.md
rules/async-parallel-decomposition.md
```

Each rule file contains:
- Brief explanation of why it matters
- Incorrect code example with explanation
- Correct code example with explanation
- Additional context and references

## Anti-Patterns Cheat Sheet

| Anti-Pattern | Rule | Fix |
|-------------|------|-----|
| God Service class | `arch-usecase-single-responsibility` | Split into focused use cases |
| @Value scattered everywhere | `spring-config-properties` | Use @ConfigurationProperties |
| N+1 queries | `db-n-plus-one` | JOIN or batch fetch |
| OFFSET pagination | `db-pagination-keyset` | Keyset/cursor pagination |
| GlobalScope.launch | `async-avoid-globalscope` | Inject CoroutineScope |
| Exceptions for business errors | `error-sealed-class` | Sealed class + Either |
| Primitive obsession (Long IDs) | `kotlin-inline-value-class` | @JvmInline value class |
| Field injection (@Autowired) | `spring-constructor-injection` | Constructor injection |
| Fat Docker image | `deploy-docker-multi-stage` | Multi-stage build |
| println debugging | `deploy-structured-logging` | Structured JSON logging |
