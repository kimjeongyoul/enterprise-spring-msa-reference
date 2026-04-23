# Global Auth & Gateway System (Spec-First MSA)

본 프로젝트는 대규모(20인 이상) 개발 팀이 협업하여 구축하는 **중앙 집중형 인증(Auth) 및 고성능 라우팅(Gateway)** 솔루션의 참조 아키텍처입니다.

## 🚀 Project Philosophy: Spec-First
이 프로젝트는 코드를 짜기 전 명세를 먼저 확정하는 **SSOT(Single Source of Truth)** 원칙을 따릅니다.
- 모든 설계 근거는 [specs/](./specs/) 폴더에 마크다운으로 기록됩니다.
- 주요 기술적 결정은 [ADR(Architecture Decision Records)](./specs/decisions/)을 통해 관리됩니다.
- 20인 이상의 개발자가 커뮤니케이션 비용 없이 협업할 수 있는 **Modular Monorepo** 구조를 지향합니다.

## 🏗 System Architecture
시스템은 마이크로서비스 아키텍처(MSA)로 설계되었으며, 모든 외부 요청은 Gateway를 통해 중앙 집중식으로 제어됩니다.

- **Gateway Service (8080)**: Spring Cloud Gateway 기반의 진입점. 모든 요청에 대한 인증 필터링 및 라우팅 수행.
- **Auth Service (8081)**: JWT 기반 사용자 인증, 권한 관리 및 토큰 발행 전담.
- **Common Lib**: 전사 표준 응답 객체 및 에러 핸들링 로직을 공유하는 공통 모듈.

## 🛠 Tech Stack
- **Framework**: Java 17, Spring Boot 3.2.4
- **Security**: Spring Security, JWT (JJWT 0.12.5)
- **Infrastructure**: Spring Cloud Gateway, Resilience4j, Redis, MySQL/H2
- **Build Tool**: Gradle Multi-project (Monorepo)
- **Deployment**: Docker, Kubernetes (ConfigMap & Secret 기반 설정 분리)

## 📁 Project Structure
```text
root/
├── specs/                  # [SSOT] 모든 설계 및 기술 결정 문서
├── common-lib/             # 모든 서비스가 공유하는 표준 모듈
├── auth-service/           # 인증 서버 (Security, JWT, JPA)
├── gateway-service/        # 게이트웨이 (Routing, Global Auth Filter)
├── build.gradle            # 전사 표준 기술 스택 정의
└── settings.gradle         # 모듈 통합 관리 설정
```

## ⚙️ Getting Started

### Prerequisites
- Java 17 이상
- Gradle 8.x 이상

### Local Development
```bash
# 전체 프로젝트 빌드
./gradlew build

# 특정 서비스 실행 (Auth)
./gradlew :auth-service:bootRun
```

### Deployment (Kubernetes)
각 서비스는 개별적으로 컨테이너화되어 K8s 클러스터에 배포될 수 있도록 설계되었습니다.
```bash
# Auth Service 배포 예시
kubectl apply -f auth-service/k8s/config.yaml
kubectl apply -f auth-service/k8s/deployment.yaml
```

## 🛡 Engineering Standard
본 프로젝트는 엄격한 엔지니어링 표준을 준수합니다. 상세 내용은 [specs/engineering.md](./specs/engineering.md)을 참조하세요.
- **Commit Convention**: `feat(scope): spec-id - description`
- **Definition of Done (DOD)**: 빌드/테스트 성공 및 명세 일치 확인 필수.

---
[AI Context: Normal | Snap: OK]
