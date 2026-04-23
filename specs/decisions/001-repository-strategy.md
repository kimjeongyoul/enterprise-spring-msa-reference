# ADR 001: Repository Strategy for 20+ Developers

## Status
Proposed (2024-04-23)

## Context
- 대상 시스템: Java Spring 기반 API Gateway 및 Auth Server
- 예상 팀 규모: 약 20명 (Auth팀, Gateway팀, 플랫폼팀 등 분리)
- 문제: 대규모 인원이 동시에 개발할 때 발생하는 커뮤니케이션 비용, 코드 파편화, 기술적 일관성 부족 해결 필요.

## Decision: Modular Monorepo (Gradle Multi-Project)
단일 저장소 내에서 도메인별 모듈을 분리하는 **Modular Monorepo** 방식을 채택한다.

### 1. Structure
- `auth-service/`: 독립 배포 가능한 인증 서비스 모듈
- `gateway-service/`: 독립 배포 가능한 게이트웨이 서비스 모듈
- `common-lib/`: 전사 표준 보안 필터, 예외 처리, 공통 DTO (플랫폼팀 관리)
- `specs/`: 모든 팀의 합의점인 SSOT 명세 폴더 (최상위 배치)

### 2. Workflow (Spec-Driven)
- 모든 기능 구현 전 `specs/blueprints/`에 설계안을 먼저 작성하고 리뷰를 거친다.
- 변경 사항은 반드시 `specs/decisions/`에 ADR 형식으로 기록한다.
- `CODEOWNERS`를 활용하여 `specs/` 및 `common-lib/` 수정에 대한 승인 권한을 엄격히 관리한다.

## Consequences
- **Positive**: 
  - 코드 재사용성 증대 (Common Lib 활용)
  - 전사적 기술 표준 강제 용이
  - 의존성 파악 및 전체 아키텍처 가시성 확보
- **Negative**:
  - CI 빌드 시간 증가 우려 (Build Cache 등으로 해결 필요)
  - 한 팀의 실수가 전체 레포지토리에 영향을 줄 가능성 (브랜치 전략 강화 필요)
