# ADR 010: Declarative Service-to-Service Communication with OpenFeign

## Status
Proposed (2024-04-23)

## Context
- 마이크로서비스 간의 동기(Synchronous) 통신이 빈번하게 발생함.
- `RestTemplate`이나 `WebClient`를 직접 사용할 경우, 반복적인 보일러플레이트 코드와 복잡한 에러 처리 로직이 비즈니스 코드에 섞여 유지보수성을 저하시킴.

## Decision: Adoption of Spring Cloud OpenFeign
선언적 방식(Declarative)의 HTTP 클라이언트인 **OpenFeign**을 전사 표준 S2S 통신 도구로 채택한다.

### 1. Rationale
- **Productivity**: 인터페이스와 어노테이션만으로 외부 API를 정의할 수 있어 개발 속도가 비약적으로 향상됨.
- **Maintainability**: 비즈니스 로직과 인프라(HTTP 호출) 로직이 분리되어 코드가 간결해짐.
- **Eco-system Integration**: Spring Cloud의 로드 밸런싱, 서킷 브레이커, 분산 트레이싱 기능을 별도 구현 없이 즉시 적용 가능함.
- **Standardization**: 20인 규모의 팀에서 서로의 API를 호출하는 방식을 단일화하여 커뮤니케이션 비용 절감.

## Consequences
- **Positive**: 
  - 가독성 및 유지보수성 증대.
  - 엔터프라이즈 기능(회로 차단 등)과의 손쉬운 연동.
- **Negative**:
  - 기본적으로 동기 방식이므로, 호출 대상 서버의 장애가 현재 서버로 전파될 위험이 있음 (Resilience4j 연동 필수).
