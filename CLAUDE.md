# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Lost Ark for Rice (LFR)** — Lost Ark 게임 경제 데이터(보석/재료 시세) 추적 및 분석 서비스. 백엔드가 Lost Ark API에서 가격 데이터를 수집·가공하고, 프론트엔드가 캔들차트로 시각화한다.

## Monorepo Structure

```
lostark-for-rice/
├── backend/          # Kotlin + Spring Boot (multi-module Gradle)
│   ├── common/               # 서비스 간 공유 DTO/contract
│   ├── integration-service/  # 외부 API 게이트웨이 + 데이터 수집기 (port 9011)
│   └── processor-service/    # 데이터 저장/조회 레이어 (port 9021, internal)
├── frontend/         # React + TypeScript + Vite SPA
├── .github/
│   ├── workflows/    # CI/CD (GitHub Actions)
│   ├── integration/  # integration-service Docker/nginx 설정
│   ├── processor/    # processor-service Docker/nginx 설정
│   └── nginx.conf    # nginx reverse proxy 설정
└── gradle/           # Gradle wrapper
```

각 서브 프로젝트의 상세 아키텍처, 빌드 명령어, 코드 컨벤션은 해당 디렉토리의 `CLAUDE.md`를 참고:
- `backend/CLAUDE.md` — 백엔드 아키텍처, 빌드/테스트, DB, Spring 프로파일, 환경변수
- `frontend/CLAUDE.md` — 프론트엔드 아키텍처, 디렉토리 구조, 스타일링, 상태관리

## Data Flow

```
[Lost Ark API] → integration-service → processor-service → [PostgreSQL/Redis]
[Frontend SPA] → nginx(/api/) → integration-service → processor-service → [PostgreSQL]
```

nginx가 `/api/` 요청은 integration-service로, `/auction/`, `/market/` 요청은 processor-service로 라우팅한다.

## CI/CD & Deployment

- **GitHub Actions**: `backend/**` 경로 변경 시 트리거, `dorny/paths-filter`로 변경된 모듈만 선택 배포
- **Docker 이미지**: GHCR (`ghcr.io/olkkani/integration`, `ghcr.io/olkkani/processor`), `eclipse-temurin:21-alpine` 기반, `linux/arm64` 플랫폼
- **Blue-Green 배포**: 각 서비스가 blue/green 컨테이너로 운영되며, nginx upstream 설정을 교체 후 `nginx -s reload`
- **네트워크**: 모든 컨테이너가 `network-integration` Docker 외부 네트워크로 통신. processor-service는 외부 포트 바인딩 없이 내부 전용

## Common Conventions

- **언어**: 코드 주석과 UI 문자열은 한국어, 코드 식별자와 문서는 영어
- **커밋 시 주의**: backend 변경은 CI가 자동 트리거되므로, common 모듈 변경 시 양쪽 서비스 영향 확인 필요
- **환경변수**: 민감 정보(API 키, DB 자격증명 등)는 GitHub Secrets → docker-compose `.env`로 주입, 절대 커밋하지 않음
