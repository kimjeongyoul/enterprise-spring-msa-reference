# ADR 005: Separate Admin Backoffice and User-Centric Order Service

## Status
Proposed (2024-04-23)

## Context
- `order-service`에 관리자(Admin) 기능을 포함할 경우, 일반 유저 권한 로직과 섞여 복잡도가 증가하고 보안 취약점이 발생할 수 있음.
- 대규모 시스템에서는 관리자 전용 대시보드와 API를 물리적으로 분리하는 것이 정석임.

## Decision: Separation of Concerns
1. **User-Centric order-service**: 일반 사용자는 본인의 주문 생성 및 조회만 가능하도록 제한한다.
2. **Backoffice Separation**: 관리자 기능(전체 주문 조회, 상태 변경 등)은 추후 별도의 `admin-service` 또는 백오피스 시스템으로 구현한다.
3. **Authorization**: Gateway가 검증하여 전달한 `X-User-Name` 헤더를 신뢰하여, 해당 사용자의 데이터만 접근하도록 강제한다.

## Consequences
- **Positive**: 
  - 일반 유저 API의 보안성 극대화.
  - 마이크로서비스의 단일 책임 원칙(SRP) 준수.
  - 관리자용 무거운 쿼리가 일반 유저 서비스 성능에 영향을 주지 않음.
- **Negative**:
  - 관리자 기능을 위해 별도의 인프라/서비스 구축 비용 발생.
