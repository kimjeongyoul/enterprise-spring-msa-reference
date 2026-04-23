# Global Auth System Architecture (Mature MSA)

## 1. 개요
본 프로젝트는 서비스 간 독립적인 배포(Independent Deployability)와 높은 가용성을 보장하는 성숙한 마이크로서비스 아키텍처(Mature MSA)를 지향합니다.

## 2. 핵심 설계 원칙
### 2.1. Thin Common Library (Protocol vs Content)
- **`common-lib`**: 전사 공통 규격(API 응답, 예외 처리 메커니즘, 트레이싱 필터) 및 인프라 SDK만 포함합니다.
- **Data Isolation**: 다국어 메시지(`messages.properties`) 및 도메인 특정 에러 코드는 각 서비스로 격리하여, 단순 텍스트 수정으로 인한 전체 재배포를 방지합니다.

### 2.2. Centralized Metadata Management
- **`common-service`**: 전사 공통 코드(상태 값, 카테고리 등)를 DB에서 관리하고 API로 서빙합니다.
- **Dynamic Update**: 코드 추가/수정 시 서비스 재배포 없이 실시간으로 모든 서비스에 반영됩니다.

### 2.3. Asynchronous Resilience (Kafka)
- **Message Queuing**: 부하 폭주 시 Kafka가 충격 흡수기(Buffer) 역할을 수행하여 시스템 붕괴를 막습니다.
- **Eventual Consistency**: 서비스 간 강한 결합을 제거하고, 이벤트를 통한 최종적 일관성을 유지합니다.

## 3. 핵심 패턴 및 메커니즘
### 3.1. Transactional Outbox Pattern
- **문제**: DB 저장과 메시지 발행의 원자성 보장 불가.
- **해결**: 비즈니스 데이터와 이벤트를 동일한 DB 트랜잭션 내의 `Outbox` 테이블에 저장하고, 별도 스케줄러가 Kafka로 발행하여 **최소 1회 전달(At-least-once delivery)**을 보장합니다.

### 3.2. Saga Pattern (Choreography)
- **보상 트랜잭션**: 비동기 흐름 중 실패 발생 시(예: 재고 부족), 반대 방향의 실패 이벤트를 발행하여 이미 완료된 작업을 원상복구(주문 취소)합니다.

## 4. 서비스 구성
| 서비스 | 역할 | 특징 |
| :--- | :--- | :--- |
| **Gateway** | 인증/인가, 트레이싱 시작 | JWT 검증, X-User-ID 및 X-Correlation-ID 주입 |
| **Common** | 메타데이터/공통 코드 관리 | DB 기반 동적 코드 서빙 |
| **Auth** | 계정 및 인증 관리 | Outbox 패턴 적용된 회원가입 |
| **Order** | 주문 관리 (Saga 시작점) | 주문 접수(Pending) 및 실패 시 보상(Cancel) 처리 |
| **Product** | 상품/재고 관리 (Saga 참여자) | 비동기 재고 차감 및 실패 시 보상 이벤트 발행 |
| **Notification** | 알림 서비스 (최종 소비자) | 비동기 이벤트 수신 후 알림 발송 |
