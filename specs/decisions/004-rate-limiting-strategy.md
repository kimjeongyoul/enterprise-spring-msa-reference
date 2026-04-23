# ADR 004: Rate Limiting Strategy for API Gateway

## Status
Proposed (2024-04-23)

## Context
- 특정 사용자의 과도한 요청(Brute-force 공격, 매크로 등)으로부터 인증 서버를 보호해야 함.
- 분산 환경(MSA)에서 여러 게이트웨이 인스턴스가 공통된 요청 횟수를 공유해야 함.

## Decision: Spring Cloud Gateway + Redis RateLimiter
Redis 기반의 **Token Bucket 알고리즘**을 사용하여 속도 제한을 구현한다.

### 1. Rationale
- **Spring Cloud Gateway 내장 필터**: `RequestRateLimiter` 필터를 활용하여 검증된 로직 사용.
- **Redis (Shared State)**: 여러 게이트웨이 인스턴스가 하나의 Redis를 바라보므로, 인스턴스가 늘어나도 전역적인 요청 제한이 가능함.
- **Key Resolver**: 클라이언트의 IP 주소를 기준으로 요청 횟수를 계산한다.

### 2. Policy (Default)
- **Replenish Rate**: 초당 10개의 토큰 생성 (초당 평균 10회 요청 허용).
- **Burst Capacity**: 최대 20개의 토큰 저장 (순간적으로 최대 20회 요청까지 허용).

## Consequences
- **Positive**: 
  - 인증 서버 부하 방지 및 보안 강화.
  - Redis를 통한 중앙 집중식 요청 관리.
- **Negative**:
  - Redis 장애 시 게이트웨이 동작에 영향을 줄 수 있음 (Fallback 전략 고려 필요).
  - 로컬 테스트 시 Redis 실행 필수.
