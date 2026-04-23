# Commerce Advanced Roadmap & TODOs

## ✅ Phase 1: Reliability & Performance (Completed)
- Distributed Locking (Redisson)
- Message Idempotency (Redis)
- CQRS Initial Setup (Redis Read Model)
- Retry & DLQ Strategy

## 🚀 Phase 2: Advanced Business Logic (In Progress)
### 1. Redis 기반 초고속 선착순 재고 시스템 (Atomic Stock)
- **Goal**: DB 부하 전 Redis에서 원자적 재고 차감.
- **Tech**: Redis Lua Script, Spring Data Redis.
- **Status**: Planning.

### 2. 주문 타임아웃 자동 취소 (Order Timeout)
- **Goal**: 미결제 주문 자동 취소 및 재고 원복.
- **Tech**: Redis Key Expiration Event or Kafka Delayed Message.
- **Status**: Planning.

## 📝 Phase 3: Reliability Hardening (TODO)
### 3. 보상 트랜잭션 대사 서비스 (Reconciliation)
- **Problem**: 비동기 메시지 유실 시 데이터 불일치 발생.
- **Logic**: 
  - 주기적으로 `orders` 테이블과 `product/stock`, `payment` 테이블 배치 비교.
  - `PENDING` 상태로 장시간 방치된 건이나, 한쪽은 성공하고 한쪽은 누락된 건을 찾아 자동 보정.
- **Status**: BACKLOG.
