# ADR 016: Centralized Internationalization (i18n) Strategy

## Status
Proposed (2024-04-23)

## Context
- 글로벌 서비스를 위해 사용자 국가별 언어 대응(다국어 처리) 필요.
- 마이크로서비스마다 개별적으로 메시지를 관리할 경우, 동일한 에러 코드에 대해 서로 다른 메시지가 노출될 위험이 있음.

## Decision: Centralized Message Management in common-lib
다국어 메시지 리소스를 `common-lib`에서 통합 관리하고, HTTP 헤더(`Accept-Language`)에 따라 동적으로 메시지를 처리한다.

### 1. Unified Message Source
- `common-lib`에 표준 메시지 파일들을 위치시켜 모든 서비스가 동일한 메시지 번들을 공유한다.
- `messages.properties` (Default), `messages_ko.properties`, `messages_en.properties` 등.

### 2. Header-based Resolution
- `LocaleContextHolder`를 활용하여 요청 헤더의 언어 설정을 자동으로 감지한다.
- 에러 발생 시 `ErrorCode`의 키값을 기반으로 메시지를 조회하여 `ApiResponse`에 담아 반환한다.

## Consequences
- **Positive**: 
  - 전사적으로 일관된 에러 메시지 및 알림 문구 유지.
  - 신규 언어 추가 시 `common-lib` 업데이트만으로 전체 서비스 적용 가능.
- **Negative**:
  - 각 서비스의 비즈니스 전용 메시지도 공통 모듈에 담길 경우 라이브러리가 비대해질 수 있음 (도메인별 메시지 파일 분리 고려 필요).
