# ADR 002: Containerization and Kubernetes Deployment Strategy

## Status
Proposed (2024-04-23)

## Context
- 시스템을 Docker Compose 외에 Kubernetes(K8s) 환경에도 개별적으로 배포할 수 있어야 함.
- 마이크로서비스의 독립적인 확장(Scalability)과 무중단 배포를 보장해야 함.

## Decision: Docker Multi-stage Build & K8s Manifests
각 서비스 모듈을 독립된 컨테이너 이미지로 빌드하고, K8s 배포를 위한 표준 매니페스트를 작성한다.

### 1. Docker Strategy
- **Multi-stage Build**: 빌드 환경(Gradle)과 실행 환경(JRE)을 분리하여 이미지 크기를 최소화하고 보안을 강화한다.
- **Base Image**: Eclipse Temurin OpenJDK 17 (JRE) 사용.

### 2. Kubernetes Strategy
- **Deployment**: 각 서비스의 복제본(Replicas) 개수와 업데이트 전략 정의.
- **Service**: 내부 통신을 위한 ClusterIP 타입 서비스 정의.
- **ConfigMap/Secret**: 환경 설정 및 민감 정보 분리 관리.

## Consequences
- **Positive**: 
  - 특정 서비스만 독립적으로 확장 가능.
  - 클라우드 네이티브 환경(AWS EKS, GCP GKE 등)에 즉시 배포 가능.
- **Negative**:
  - 관리해야 할 설정 파일(YAML) 증가.
  - 로컬 테스트 시 K8s 시뮬레이터(minikube 등) 필요.
