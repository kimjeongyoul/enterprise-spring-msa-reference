# Architecture Specification: Global Auth & Gateway System

## 1. System Overview
본 시스템은 대규모(20인 이상) 개발 팀이 협업하여 구축하는 **중앙 집중형 인증(Auth) 및 라우팅(Gateway)** 솔루션입니다. 
- 모든 서비스 요청은 Gateway를 통해 단일 진입점을 가지며, 
- 모든 보안 및 권한 검증은 Auth 서버와 연동된 Gateway Filter에서 처리됩니다.

## 2. Technical Stack & Rationale
- **Backend Framework**: **Java 17 / Spring Boot 3.x** - 산업 표준의 안정성 및 강력한 생태계 활용.
- **Gateway**: **Spring Cloud Gateway** - 비동기 Non-blocking 기반의 고성능 라우팅 및 필터링.
- **Security**: **Spring Security + JWT (JSON Web Token)** - Stateless 인증 방식 채택.
- **Persistence**: 
  - **MySQL**: 사용자 정보 및 권한 영속성 관리.
  - **Redis**: Refresh Token 관리 및 Rate Limiting을 위한 고속 캐시.
- **Tooling**: **Gradle Multi-Project** - 20인 이상 규모의 모듈형 모노레포 관리.

## 3. Layered Architecture (Domain Driven)
- **Edge Layer (Gateway)**: 요청 라우팅, 로드 밸런싱, 중앙 인증 필터 적용.
- **Core Layer (Auth Service)**: 로그인/회원가입, 토큰 발행(JWT), 사용자 권한 관리.
- **Infrastructure Layer**: DB, Redis, 외부 API 연동을 담당하며, 인터페이스 기반으로 추상화.

## 4. Key Decisions (ADR)
- [ADR 001: Repository Strategy for 20+ Developers](./decisions/001-repository-strategy.md) - 20인 규모 협업을 위한 모듈형 모노레포 채택.
- [ADR 002: Centralized Auth Filter (To be created)] - 게이트웨이 기반의 통합 인증 처리 결정.

## 5. Scalability & Resilience
- **Horizontally Scalable**: 모든 서버는 무상태(Stateless)로 설계되어 트래픽 증가 시 인스턴스 확장이 용이함.
- **Fault Tolerance**: Resilience4j 등을 활용하여 특정 서비스 장애가 전체 시스템으로 전파되는 것을 방지.