# ADR 014: Standardized Pagination Strategy for Large Data Sets

## Status
Proposed (2024-04-23)

## Context
- 리뷰, 게시판 등 데이터가 지속적으로 축적되는 도메인에서 전체 데이터를 한 번에 조회할 경우 메모리 부족 및 네트워크 성능 저하 발생 우려.
- 사용자에게 효율적으로 데이터를 분할 전달하기 위한 표준화된 페이징 기법 필요.

## Decision: Spring Data JPA Offset-based Pagination
Spring Data JPA가 제공하는 **`Pageable`** 인터페이스를 사용하여 오프셋 기반 페이징을 구현한다.

### 1. Request Standard
- `page`: 조회할 페이지 번호 (0부터 시작).
- `size`: 한 페이지당 데이터 개수.
- `sort`: 정렬 기준 필드 및 방향 (예: `createdAt,desc`).

### 2. Response Standard (Wrapped in ApiResponse)
- **`Page<T>`** 객체를 활용하여 실제 데이터 목록뿐만 아니라 전체 페이지 수, 전체 데이터 개수, 현재 페이지 번호 등의 메타데이터를 함께 반환한다.

## Consequences
- **Positive**: 
  - 서버 부하 감소 및 응답 속도 향상.
  - 정석적인 페이징 구현으로 프론트엔드와의 협업 용이.
- **Negative**:
  - 데이터가 아주 방대해질 경우(수백만 건 이상) 오프셋 방식의 성능 저하 발생 가능 (이후 커서 기반 페이징으로 고도화 검토 가능).
