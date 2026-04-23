# ADR 003: Externalized Configuration Management

## Status
Proposed (2024-04-23)

## Context
- 환경(Dev, Prod)에 따라 달라지는 설정값 관리 필요.
- JWT Secret Key, DB Password 등 민감 정보의 보안 강화 필요.
- 애플리케이션 빌드 없이 설정 변경만으로 시스템 동작을 제어해야 함.

## Decision: Kubernetes ConfigMap & Secret
Kubernetes의 표준 오브젝트인 ConfigMap과 Secret을 활용하여 설정을 외부화한다.

### 1. ConfigMap (일반 설정)
- 로그 레벨 (LOGGING_LEVEL)
- 서비스 URL (AUTH_SERVICE_URL)
- 활성화된 프로파일 (SPRING_PROFILES_ACTIVE)

### 2. Secret (민감 정보)
- JWT 서명 키 (JWT_SECRET_KEY)
- 데이터베이스 비밀번호 (DB_PASSWORD)

## Consequences
- **Positive**: 
  - 보안 강화 (Secret은 암호화되어 저장 및 전달됨).
  - 유연성 증대 (동일한 이미지를 설정만 바꿔서 여러 환경에 배포 가능).
- **Negative**:
  - 관리해야 할 K8s 오브젝트가 늘어남.
  - 로컬 개발 환경과의 설정 동기화 필요.
