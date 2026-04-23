# ADR 009: Product and Branch Management Strategy

## Status
Proposed (2024-04-23)

## Context
- 시스템이 확장됨에 따라 상품 정보(가격, 재고)뿐만 아니라 이를 판매/배송하는 지점(Branch/Store) 정보 관리가 필요함.
- `order-service`가 주문 처리 시 최신 상품 정보 및 지점별 재고 상태를 확인해야 함.

## Decision: Consolidated Product & Branch Service
상품과 지점 정보를 **`product-service`**라는 하나의 도메인 서비스로 통합하여 관리한다.

### 1. Data Model
- **Product**: 상품 기본 정보 (이름, 기본가, 카테고리).
- **Branch**: 지점 정보 (지점명, 위치, 운영 상태).
- **Stock**: 지점별 상품 재고 (어느 지점에 어떤 상품이 몇 개 있는지).

### 2. Service-to-Service Communication (Feign Client)
- `order-service`는 주문 생성 시 **Spring Cloud Feign**을 사용하여 `product-service`에 실시간 재고 확인 및 상품 정보를 요청한다.
- 동기(Synchronous) 통신 방식을 채택하되, 회로 차단기(Circuit Breaker)를 적용하여 `product-service` 장애 시 주문 서비스의 연속성을 보장한다.

## Consequences
- **Positive**: 
  - 상품과 지점/재고 간의 강력한 데이터 일관성 유지.
  - 마이크로서비스 간의 실질적인 서버 간 통신(S2S) 패턴 구현 및 증명.
- **Negative**:
  - `product-service` 장애 시 주문 생성에 영향을 줄 수 있음 (Fallback 처리 필수).
