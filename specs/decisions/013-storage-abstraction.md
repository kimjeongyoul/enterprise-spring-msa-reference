# ADR 013: Plug-in Storage Abstraction for Media Files

## Status
Proposed (2024-04-23)

## Context
- 리뷰 서비스 등에서 이미지 및 파일 업로드 기능이 필수적으로 요구됨.
- 개발 환경(Local), 테스트 환경, 운영 환경(S3 등)에 따라 파일 저장 매체가 상이함.
- 특정 클라우드 서비스(AWS S3)에 강하게 결합될 경우, 인프라 변경 시 대규모 코드 수정이 불가피함.

## Decision: Storage Service Abstraction
파일 저장 로직을 인터페이스로 추상화하여 **플러그인 방식**으로 교체 가능하도록 설계한다.

### 1. StorageService Interface
- `String upload(MultipartFile file)`: 파일을 저장하고 접근 가능한 URL 또는 식별자 반환.
- `void delete(String fileId)`: 파일 삭제.
- `String getUrl(String fileId)`: 파일의 실제 접근 경로 조회.

### 2. Multi-Implementation Strategy
- **Local Implementation**: 로컬 파일 시스템 사용 (개발/테스트 용).
- **S3 Implementation**: Amazon S3 SDK 사용 (운영 용).
- **Profile-based Selection**: Spring Profile(`dev`, `prod`)에 따라 빈(Bean)을 동적으로 주입한다.

## Consequences
- **Positive**: 
  - 특정 인프라에 종속되지 않는 유연성 확보.
  - 인프라 비용 및 환경에 따른 최적의 스토리지 선택 가능.
- **Negative**:
  - 파일 메타데이터 관리 및 동기화 로직의 복잡성 소폭 증가.
