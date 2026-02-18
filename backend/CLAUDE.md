# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Lost Ark game economy tracker backend. Collects auction/market item prices from the Lost Ark developer API on a Quartz schedule, stores OHLC (Open-High-Low-Close) candlestick data, and serves it to a frontend.

**Tech stack:** Kotlin 1.9.25, Java 21, Spring Boot 3.5.3, Gradle (Kotlin DSL), PostgreSQL, Redis, jOOQ, Flyway

## Build & Test Commands

```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :integration-service:build
./gradlew :processor-service:build

# Run tests
./gradlew test
./gradlew :processor-service:test
./gradlew :integration-service:test

# Run a single test class
./gradlew :processor-service:test --tests "io.oikkani.processorservice.application.service.AuctionSnapshotServiceTest"

# Lint
./gradlew ktlintCheck          # check only
./gradlew ktlintFormat          # auto-fix

# Generate jOOQ classes (requires Docker for PostgreSQL container)
./gradlew :processor-service:generateJooqClasses
```

## Architecture

**Hexagonal Architecture (Ports & Adapters)** across both service modules:
- `application/port/inbound/` — use case interfaces (driven ports)
- `application/port/outbound/` — repository/notification interfaces (driving ports)
- `application/service/` — use case implementations
- `infrastructure/inbound/` — REST controllers, Quartz jobs (primary adapters)
- `infrastructure/outbound/` — WebClients, JPA repos, jOOQ repos, Redis repos (secondary adapters)

### Module Structure

**`:common`** — Shared DTOs used as the contract between the two services. Contains `dto/contract/` (AuctionItemPrice, MarketItemPrice, CandleChart, etc.) and `api/` (response types).

**`:integration-service`** (port 9011 local) — Public-facing API gateway + data collector:
- Polls Lost Ark API (`https://developer-lostark.game.onstove.com`) via Quartz cron jobs (prod profile only)
- Fetches auction gems (6 types) and market items (fusion materials, relic engraving recipes)
- Forwards collected data to processor-service via internal HTTP
- Handles Discord OAuth2 login + JWT auth
- Sends error notifications to Discord webhook
- `BaseClient` provides WebClient retry policy: 3 retries/1s backoff, 429 = 1min delay, Discord alert on exhaust

**`:processor-service`** (port 9021 local, internal-only in prod) — Data storage + query layer:
- Persists price snapshots and maintains OHLC records per item per day
- Uses JPA for writes and jOOQ for complex reads
- Manages JWT refresh tokens in Redis
- jOOQ classes auto-generated from Flyway migrations via `dev.monosoul.jooq-docker` (output: `build/generated-jooq/`)

### Data Flow

```
Quartz → integration-service → Lost Ark API → processor-service → PostgreSQL
Frontend → integration-service → processor-service → PostgreSQL
```

## Database

- **PostgreSQL**: OHLC prices, snapshots, price changes (Flyway migrations in `processor-service/src/main/resources/db/migration/`)
- **Redis**: JWT refresh tokens (`@RedisHash("refresh_token")`, 30-day TTL)
- **Local dev**: H2 (MODE=PostgreSQL) + embedded Redis

Key tables: `daily_auction_item_ohlc_prices`, `daily_market_item_ohlca_prices`, `auction_item_price_snapshots`, `market_item_price_snapshots`, `item_previous_price_changes`

## Code Quality

- **ktlint 1.8.0** and **detekt 1.23.7** enforced on all subprojects; `check` depends on `ktlintCheck`
- Tests use JUnit 5 + MockK + Kotest; processor-service tests use Testcontainers (PostgreSQL + Redis)
- Tomcat excluded; Undertow is the embedded server

## Spring Profiles

| Profile group | What activates |
|---|---|
| `local` | H2 in-memory DB, embedded Redis, seed data loaded |
| `prod` | PostgreSQL, Redis server, Quartz scheduler enabled |

## Environment Variables (prod)

- `LOSTARK_API_KEY`, `JWT_SECRET`, `DISCORD_WEBHOOK_URL`, `DISCORD_CLIENT_ID`, `DISCORD_CLIENT_SECRET`
- `DB_CONNECTION_URL`, `DB_USER`, `DB_PASSWORD`, `DB_REDIS_HOST`, `DB_REDIS_PORT`, `DB_REDIS_PASSWORD`

## Deployment

Blue-green via Docker on shared network `network-integration`. Both services build to `eclipse-temurin:21-alpine` images. processor-service has no external port binding (internal only behind nginx).
