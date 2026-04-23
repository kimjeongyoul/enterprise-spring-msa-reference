# MSA Platform V2 Migration Blueprint

## 1. 아키텍처 비전 (Target Architecture)
모든 산업군에서 가져다 쓸 수 있도록 의존성을 계층화한 **"Modular MSA Platform"**을 구축한다.

- **Level 1: Infra (Core Engine)**
  - `common-core`: DTO, ErrorCode 인터페이스, Trace-ID Context (모든 서비스 공통)
  - `common-web`: MVC 전용 인프라, GlobalExceptionHandler, FeignConfig, Resilience4j
  - `gateway-service`: WebFlux 기반 관문, 중앙 인증, 리액티브 에러 핸들링
  - `common-service`: DB 기반 공통 코드(Metadata) 서빙
- **Level 2: Base Services (The Starter)**
  - `auth-service`: JWT 기반 보안 및 Transactional Outbox 회원가입
  - `notification-service`: 비동기 이벤트 기반 알림 전송
- **Level 3: Commerce Verticals (Advanced Case)**
  - `order`, `product`, `payment`: Saga 패턴(Choreography) 기반의 분산 트랜잭션 도메인

## 2. 반드시 기억해야 할 핵심 해결책 (Lessons Learned)
- **Gateway Conflict**: `common-lib`에 MVC 의존성이 섞이면 게이트웨이가 죽음. 반드시 `common-core`와 `common-web`을 물리적으로 분리하여 게이트웨이는 `core`만 참조할 것.
- **Reactive Error Handling**: 게이트웨이는 `@RestControllerAdvice` 대신 `ErrorWebExceptionHandler`를 직접 구현해야 전사 표준 `ApiResponse`를 유지할 수 있음.
- **Saga Logic**: 
  - `OrderCreatedEvent` -> `Product` (재고차감) & `Payment` (결제)
  - 실패 시 `OrderFailedEvent` 발행 -> `Order` (취소) & `Product` (재고복구)

## 3. 이관 가이드 (Migration Sequence)
1. **Gradle Root 초기화**: `v2-tiered` 구조로 `settings.gradle` 구성.
2. **Infra 정립**: `common-core`를 가장 먼저 생성하여 모든 규격의 뿌리를 내림.
3. **Gateway 구축**: 리액티브 엔진 충돌 없이 기동되는지 최우선 검증.
4. **Commerce 이관**: 기존의 `OrderService` 내 Saga 로직과 `OutboxScheduler`를 새 구조에 안착.

## 4. 작업 시작 지시 (Next Steps for AI)
"230401 폴더의 `MIGRATION_BLUEPRINT.md`를 읽었으니, 이제 1단계인 계층형 Gradle 루트 설정과 `common-core` 구축부터 시작해줘."
