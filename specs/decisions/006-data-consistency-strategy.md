# ADR 006: Data Consistency and Snapshot Strategy in MSA

## Status
Proposed (2024-04-23)

## Context
- 마이크로서비스 간 DB가 분리(`auth-db`, `order-db`)되어 있어 직접적인 SQL Join이 불가능함.
- 주문 서비스에서 상품 정보를 보여주기 위해 매번 `product-service`를 호출하는 것은 성능 및 가용성 측면에서 위험함.
- 시간이 지나 상품 정보(가격, 이름 등)가 변경되더라도, 과거 주문 내역은 주문 당시의 정보를 유지해야 함 (Audit requirement).

## Decision: Data Snapshotting & Shared Common Codes
데이터의 독립성과 무결성을 위해 다음 전략을 채택한다.

### 1. Data Snapshotting (Denormalization)
- `order-service`는 주문 생성 시점에 필요한 최소한의 상품 정보(상품명, 가격 등)를 자신의 DB에 **스냅샷** 형태로 저장한다.
- 이 방식은 서비스 간의 강한 결합(Runtime Dependency)을 제거하고, 과거 데이터의 불변성을 보장한다.

### 2. Shared Common Codes via common-lib
- 주문 상태(OrderStatus), 에러 코드 등 시스템 전반에서 통용되는 공통 상수는 `common-lib`에서 통합 관리한다.
- 모든 마이크로서비스는 이 라이브러리를 참조하여 타입 안전성(Type Safety)을 확보한다.

## Consequences
- **Positive**: 
  - 타 서비스 장애 시에도 주문 내역 조회 가능 (High Availability).
  - 과거 주문 데이터의 신뢰성 보장 (Immutable History).
  - 조인 없는 빠른 조회 성능.
- **Negative**:
  - 데이터 중복 저장 발생.
  - 공통 코드 변경 시 모든 서비스의 재빌드/재배포 필요.
