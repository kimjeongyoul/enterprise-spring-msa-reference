# ADR 011: Asynchronous Event-Driven Notification Strategy

## Status
Proposed (2024-04-23)

## Context
- 회원가입, 주문 완료, 비밀번호 변경 등 다양한 서비스에서 알림(이메일, SMS, 푸시) 발송 요구사항 발생.
- 알림 발송은 외부 API 호출이 수반되며 응답 속도가 느릴 수 있음.
- 알림 서버의 장애가 핵심 비즈니스(회원가입, 주문)의 실패로 이어져서는 안 됨.

## Decision: Asynchronous Event Propagation
알림 처리를 핵심 비즈니스 로직과 분리하여 **비동기 이벤트 방식**으로 처리한다.

### 1. Rationale
- **Decoupling**: 주문 서비스는 알림 서비스가 살았는지 죽었는지 몰라도 됨. (느슨한 결합)
- **User Experience**: 사용자는 알림이 실제로 발송될 때까지 기다리지 않고 즉시 응답을 받음. (Non-blocking)
- **Scalability**: 알림 요청이 몰릴 때 알림 서비스만 별도로 확장하여 처리 가능.

### 2. Implementation Strategy (Phase 1)
- 초기 단계에서는 **Spring `ApplicationEvent`**를 활용하여 내부 이벤트로 처리한다.
- 향후 부하 증가 시 코드 변경 없이 **Kafka** 또는 **RabbitMQ** 메시지 브로커로 즉시 전환할 수 있도록 인터페이스를 추상화한다.

## Consequences
- **Positive**: 
  - 핵심 서비스의 안정성 및 가용성 향상.
  - 시스템 전반의 응답 속도 개선.
- **Negative**:
  - 데이터의 최종 일관성(Eventual Consistency) 문제 고려 필요 (알림이 조금 늦게 갈 수 있음).
  - 이벤트 유실 방지를 위한 재시도 로직(Retry) 관리 필요.
