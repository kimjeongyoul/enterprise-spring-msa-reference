# ADR 008: CQRS and Read-Model Driven Architecture Roadmap

## Status
Proposed (2024-04-23)

## Context
- 시스템 확장 시 여러 마이크로서비스(`Auth`, `Order`, `Product`)에 분산된 데이터를 통합 조회하는 성능 저하 문제 예상.
- API Gateway에서의 실시간 API Composition 방식은 서비스 간의 의존성(Runtime Dependency) 및 네트워크 오버헤드를 유발함.
- 복잡한 검색 조건(예: 회원 정보 + 주문 상태 + 상품 카테고리 등)은 관계형 DB(RDB)의 조인(Join)으로 해결하기에 한계가 있음.

## Decision: Roadmap to Event-Driven CQRS (Read-Model Driven)
향후 데이터 조회 성능 극대화를 위해 **CQRS(Command Query Responsibility Segregation)** 패턴으로의 전환 로드맵을 확정한다.

### 1. Read-Model Driven Design (Query-First)
- 데이터를 저장하는 방식보다 **"사용자가 화면에서 무엇을 보고 싶어 하는가?"**를 우선 정의한다.
- 조회에 필요한 모든 필드를 미리 합친 **통합 JSON 문서(Denormalized Document)** 구조를 확정한다.

### 2. Event-Driven Sync (Kafka)
- `Auth`, `Order` 서비스 등에서 데이터 변경(CUD) 발생 시 이벤트를 발행한다.
- 전담 컨슈머(Consumer)가 이벤트를 수집하여 미리 정의된 **Read-Model 스펙**에 맞춰 데이터를 가공한다.

### 3. Integrated Read Store (Elasticsearch)
- 가공된 통합 데이터를 조인이 필요 없는 검색 전용 엔진(Elasticsearch)에 저장하여 0.1초 내외의 초고속 조회를 보장한다.

## Consequences
- **Positive**: 
  - 조인 없는 압도적인 조회 성능 확보.
  - 마이크로서비스 간의 런타임 의존성 제거 (서로 다른 서버가 죽어도 조회는 가능).
  - 복잡한 다차원 검색 요구사항 완벽 대응.
- **Negative**:
  - 데이터 지연 발생(Eventual Consistency) 감수 필요.
  - 메시지 브로커(Kafka) 및 검색 엔진 운영 비용 및 기술 복잡도 증가.
