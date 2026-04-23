# Enterprise Spring MSA Reference (Advanced Commerce)

본 프로젝트는 대규모 트래픽과 복잡한 분산 트랜잭션을 처리해야 하는 현대적 커머스 시스템을 위한 **성숙한 마이크로서비스 아키텍처(Mature MSA) 참조 모델**입니다.

## 🏗 아키텍처 핵심 설계 (The Architecture)

### 1. 계층형 공통 라이브러리 (Modular SDK)
*   **`common-core`**: 모든 서비스(Gateway 포함)의 근간. DTO, ErrorCode 규격, Trace-ID 및 UserContext, Kafka 이벤트 스키마 포함. (JPA/Web 의존성 제거)
*   **`common-web`**: MVC 기반 서비스용 인프라 SDK. GlobalExceptionHandler, 로깅 필터, Feign/Resilience4j 설정, 분산 락 유틸리티 포함.

### 2. 분산 트랜잭션 및 정합성 (Data Consistency)
*   **Saga Pattern (Choreography)**: Kafka를 통한 비동기 워크플로우 제어 및 실패 시 자동 보상 트랜잭션(주문 취소/재고 복구).
*   **Transactional Outbox**: DB 저장과 메시지 발행의 원자성을 보장하여 메시지 유실 원천 차단.
*   **Distributed Locking (Redisson)**: Redis 기반 분산 락을 통해 대량 동시 요청 상황에서도 초고속 재고 정합성 유지.
*   **Idempotency Manager**: Redis 기반 멱등성 체크로 메시지 중복 처리 및 오작동 방지.

### 3. 성능 및 안정성 (Performance & Reliability)
*   **CQRS (Read Model)**: 쓰기(DB)와 조회(Redis) 모델을 분리하여 폭주 시에도 1ms 대의 고속 조회 성능 확보.
*   **Redis Throttling**: 실시간 처리량 제어(Rate Limiting)를 통해 배후 시스템 과부하 방지.
*   **Order Sync Scheduler**: 비동기 메시지 유실에 대비한 주기적 상태 대사(Polling) 및 자동 정합성 복구.
*   **Kafka Retry & DLQ**: 일시적 장애 시 자동 재시도 및 지속 실패 시 장애 메시지 격리.

## 🚀 시스템 용량 및 지표 (Capacity)
*   **주문 처리량 (TPS)**: 초당 2,000건+ (Kafka Buffering 적용)
*   **조회 성능**: 초당 10,000건+ (Redis Read Model 적용)
*   **최대 동시 접속**: 약 50,000명 (Throttling 및 Queueing 적용)
*   **데이터 정합성**: 99.99% (Saga + Reconciliation 적용)

## 🛠 서비스 구성
| 모듈 | 역할 | 핵심 기술 |
| :--- | :--- | :--- |
| **Gateway** | 중앙 관문 | WebFlux, JWT Auth, Trace-ID Injection |
| **Common** | 메타데이터 관리 | Centralized Common Codes (DB based) |
| **Auth** | 인증/인가 | JWT, Transactional Outbox |
| **Order** | 주문 오케스트레이터 | Saga Control, CQRS Sync, Throttling |
| **Product** | 재고 관리 | Redis Lua Script, Distributed Lock |
| **Payment** | 결제 처리 | Saga Participant, Status Polling |
| **Notification** | 알림 서비스 | Async Event Consumer |

## 🏁 실행 및 테스트 가이드
1.  **전체 빌드**: `./gradlew clean bootJar`
2.  **인프라 기동**: `docker-compose up -d --build`
3.  **통합 테스트**: `./test-saga.ps1` 실행 (주문-재고-결제-알림 및 보상 트랜잭션 로그 확인)

---
**Template Version**: 본 참조 모델의 계층화된 템플릿 버전은 [여기](https://github.com/kimjeongyoul/enterprise-spring-msa-template)에서 확인할 수 있습니다.
