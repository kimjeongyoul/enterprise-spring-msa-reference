# ADR 020: Reliability Infrastructure (Locking & Idempotency) for Saga

## Context
Saga 패턴을 통한 비동기 트랜잭션 처리 시, 네트워크 지연으로 인한 **메시지 중복(Duplicate)**과 대량 주문 시 **데이터 경합(Race Condition)** 문제가 발생할 수 있습니다. 이를 해결하지 않으면 Saga의 정합성이 깨질 수 있습니다.

## Decision
Saga 패턴의 각 단계가 안전하게 실행되도록 기술적 안전장치를 도입합니다.

1. **Distributed Locking (Redisson)**
   - 역할: 동일한 자원(예: 상품 재고)에 대한 동시 접근을 제어합니다.
   - 적용: `product-service`의 재고 차감 로직.
   - 효과: 선착순 이벤트 등 폭주 상황에서 정확히 수량만큼만 판매됨을 보장합니다.

2. **Idempotency Management (Redis)**
   - 역할: 동일한 메시지가 여러 번 들어와도 한 번만 처리되도록 보장합니다.
   - 적용: Kafka 리스너의 시작 지점.
   - 효과: 네트워크 오류로 인한 메시지 재전송 시 중복 재고 차감이나 중복 결제를 방지합니다.

3. **Combined Workflow in Saga**
   - [Event Received] -> [Idempotency Check] -> [Acquire Distributed Lock] -> [Business Logic] -> [Release Lock] -> [Mark Processed]

## Consequences
- **Positive**: 분산 환경에서 완벽한 데이터 정합성 달성, 시스템의 예측 가능성 증가.
- **Negative**: Redis 의존성 증가, 락 획득 시 소폭의 지연 발생 가능성.
