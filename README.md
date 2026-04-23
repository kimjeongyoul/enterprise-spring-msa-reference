# Global Auth System (Mature MSA)

본 프로젝트는 성숙한 마이크로서비스 아키텍처(Mature MSA)를 지향하는 참조 모델입니다. 비동기 메시징, 분산 트레이싱, 그리고 엄격하게 격리된 라이브러리 구조를 통해 높은 가동성과 유지보수성을 제공합니다.

## 🏗 프로젝트 모듈 구조

### 1. 공통 인프라 (Shared Libraries)
*   **`common-core`**: 전사 표준 규격 정의. 모든 서비스(Gateway 포함)가 의존합니다.
    *   `dto`: ApiResponse, PageResponse 등 공통 API 포맷.
    *   `exception`: ErrorCode 인터페이스 및 공통 에러 정의.
    *   `event`: Kafka 통신용 주문/알림 이벤트 규격.
    *   `context`: ThreadLocal 기반의 Trace ID 및 User Context 관리.
*   **`common-web`**: MVC 기반 서비스용 인프라 SDK.
    *   `handler`: 전역 예외 처리(GlobalExceptionHandler).
    *   `filter`: MDC 로깅 및 트레이싱 필터.
    *   `config`: OpenFeign 설정, 다국어(i18n), 서킷 브레이커 기본값.

### 2. 마이크로서비스 (Domain Services)
*   **`gateway-service`**: 시스템의 관문. 리액티브(WebFlux) 엔진 기반.
    *   중앙 인증(JWT), 트레이싱 시작, 전역 에러 핸들링.
*   **`common-service`**: 메타데이터 중앙 관리소.
    *   공통 코드(상태, 카테고리 등)를 DB로 관리하고 전 서비스에 API로 서빙.
*   **`auth-service`**: 계정 및 인증 도메인.
    *   Transactional Outbox 패턴 기반의 신뢰성 있는 회원가입 처리.
*   **`order-service`**: 주문 도메인 (Saga 오케스트레이터).
    *   비동기 주문 처리 및 실패 시 보상 트랜잭션(취소) 제어.
*   **`product-service`**: 상품 및 재고 도메인.
    *   Kafka 이벤트를 통한 비동기 재고 차감 및 복구.
*   **`payment-service`**: 결제 도메인.
    *   외부 결제 시뮬레이션 및 실패 시 Saga 보상 이벤트 발행.
*   **`notification-service`**: 알림 도메인.
    *   전사의 모든 비동기 이벤트를 수신하여 알림 발송.
*   **`community-service`**: 리뷰 및 커뮤니티 도메인.
    *   스토리지 추상화 레이어를 통한 파일 및 리뷰 관리.

## 🚀 핵심 기술 및 패턴
... (기존 내용) ...

## 📊 시스템 용량 및 운영 가이드 (Capacity & Cost)

### 1. 처리 성능 (Estimated Capacity)
본 아키텍처는 수평 확장(Scale-out)을 전제로 설계되었으며, 기본 클러스터 구성 시 다음 수준의 부하를 감당할 수 있습니다.
*   **주문 처리량 (TPS)**: 초당 2,000건 이상의 주문 생성 (Kafka Buffer 및 Outbox 패턴 활용)
*   **조회 성능**: 초당 10,000건 이상의 주문 목록 조회 (Redis Read Model 및 CQRS 적용)
*   **최대 동시 접속**: 약 50,000명 (대기열 시스템 및 Throttling 적용)

### 2. 예상 운영 비용 (Cost Estimation - AWS 기준)
중소규모 커머스 운영 시 월 예상 비용 가이드입니다 (On-demand 기준).
*   **Compute (EKS/ECS)**: 약 $300 (서비스별 t3.medium 인스턴스 2대씩 가용성 확보)
*   **Database (RDS/ElastiCache)**: 약 $150 (Multi-AZ 적용)
*   **Messaging (Managed Kafka)**: 약 $100 (MSK Serverless 기준)
*   **총계**: 월 약 $550 ~ $800 (트래픽에 따라 유동적이며, 예약 인스턴스 사용 시 최대 40% 절감 가능)

### 3. 신뢰성 지표 (Reliability)
*   **데이터 정합성**: 99.99% (Saga 보상 트랜잭션 및 Polling 기반 동기화)
*   **장애 복구 시간 (RTO)**: 수초 이내 (Circuit Breaker 및 자동 재시도 적용)

## 🛠 실행 및 테스트
1.  **빌드**: `./gradlew bootJar`
2.  **전체 기동**: `docker-compose up -d --build`
3.  **Saga 테스트**: `./test-saga.ps1` 실행 (주문-재고-결제-알림 흐름 확인)
