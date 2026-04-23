# ADR 007: Centralized Metadata & Code Management Strategy

## Status
Proposed (2024-04-23)

## Context
- 단순 Enum으로 관리하기 힘든 방대한 양의 공통 코드(국가, 통화, 은행 코드 등)가 존재함.
- 새로운 코드 추가 시 마다 전체 마이크로서비스를 재배포하는 것은 비효율적임.
- 코드에 대한 설명이나 다국어 지원(i18n)이 필요함.

## Decision: Externalized Metadata via Config/Database
공통 코드를 서비스의 로직(Code)에서 분리하여 데이터(Data)로 관리한다.

### 1. Hierarchical Code Structure
- **Group Code**: 코드의 묶음 (예: `CURRENCY`, `COUNTRY`)
- **Detail Code**: 실제 값 (예: `KRW`, `KOR`)
- **Metadata**: 코드의 설명, 정렬 순서, 사용 여부 등.

### 2. Delivery & Caching
- **Source of Truth**: 중앙 DB 또는 `common-lib` 내의 전역 설정 파일.
- **Cache-Aside Pattern**: 각 서비스는 최초 요청 시 중앙에서 코드를 로드하여 로컬 캐시(Caffeine)에 저장하고 사용한다.

## Consequences
- **Positive**: 
  - 서버 재배포 없는 코드 추가/수정 가능.
  - 시스템 전반의 코드 일관성 보장.
- **Negative**:
  - 코드 조회 시 초기 로딩 비용 및 캐시 동기화 관리 필요.
