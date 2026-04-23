# ADR 015: Standardization of Environment Variables and Configuration

## Status
Proposed (2024-04-23)

## Context
- 마이크로서비스마다 설정값 명칭이 다를 경우(예: `DB_URL` vs `DATASOURCE_ADDR`), 인프라 배포 및 운영 시 혼선 발생.
- 보안 정보(Secret)와 일반 설정(Config)의 구분 명확화 필요.

## Decision: Hierarchical Environment Variable Naming
전사적으로 공통된 환경 변수 명명 규칙을 적용한다.

### 1. Naming Convention (Uppercase with Underscore)
- **Infrastructure**: `{SERVICE_NAME}_{PROPERTY}` (예: `AUTH_DB_URL`, `REDIS_HOST`)
- **Security**: `{SERVICE_NAME}_SECRET_{KEY}` (예: `AUTH_SECRET_JWT_KEY`)
- **Business**: `{SERVICE_NAME}_CONFIG_{KEY}` (예: `ORDER_CONFIG_MAX_LIMIT`)

### 2. Common Property Structure
모든 서비스의 `application.yml`은 다음 구조를 표준으로 삼는다.
- `spring.datasource.url`: `${GLOBAL_DB_URL}` (기본값 제공 가능)
- `spring.profiles.active`: `${GLOBAL_PROFILE}`

## Consequences
- **Positive**: 
  - K8s Helm Chart나 Docker Compose 설정의 재사용성 증대.
  - 신규 서비스 추가 시 인프라 설정 비용 절감.
- **Negative**:
  - 기존 서비스들의 설정 키를 변경해야 하는 마이그레이션 비용 발생.
