# ADR 012: Reliable Messaging with Transactional Outbox Pattern

## Status
Proposed (2024-04-23)

## Context
- 비즈니스 로직(DB 저장)과 이벤트 발행(Message Publish) 사이의 원자성(Atomicity) 보장 필요.
- DB 저장은 성공했으나 이벤트 발행이 실패할 경우, 시스템 간 데이터 불일치 및 이벤트 유실 발생.

## Decision: Transactional Outbox Pattern (Polling Publisher)
이벤트 유실을 방지하기 위해 각 서비스의 로컬 DB를 활용한 아웃박스 패턴을 도입한다.

### 1. Implementation
- **Outbox Table**: 각 마이크로서비스는 자신의 DB에 이벤트를 임시 저장할 `OUTBOX` 테이블을 소유한다.
- **Single Transaction**: 비즈니스 데이터와 이벤트 데이터를 동일한 DB 트랜잭션으로 처리한다.
- **Polling Relay**: 배경 스케줄러가 `OUTBOX` 테이블에서 미처리된 이벤트를 주기적으로 읽어 발행하고, 성공 시 완료 처리한다.

### 2. Failure Handling
- 메시지 발행 실패 시, 다음 스케줄링 주기에서 자동으로 재시도된다.
- 이를 통해 '최소 한 번 전달(At-least-once delivery)'을 보장한다.

## Consequences
- **Positive**: 
  - 이벤트 유실 0% 보장.
  - 마이크로서비스 간의 강력한 데이터 일관성(Reliability) 확보.
- **Negative**:
  - DB 부하 소폭 증가 (지속적인 폴링).
  - 메시지 중복 발행 가능성 (수신 측에서 이데포턴시/중복 제거 처리 필요).
