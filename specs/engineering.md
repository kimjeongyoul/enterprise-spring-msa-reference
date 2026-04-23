# Engineering Standard Specification

## 🛠 Commit Convention (Spec-Driven)
모든 커밋은 작업의 성격과 대상 명세를 명확히 식별할 수 있어야 합니다.

### Format
`<type>(<scope>): <spec-id> - <description>`

### Types
- **feat**: 새로운 기능 명세 구현
- **spec**: 명세서(Blueprints/Architecture) 작성 및 수정
- **refactor**: 명세 변경 없이 코드 구조 개선
- **fix**: 명세와 불일치하는 버그 수정
- **docs**: 문서 수정

### Example
- `feat(auth): login-spec - implement JWT validation logic`
- `spec(api): payment-blueprint - define refund interface`
- `fix(core): architecture-spec - resolve context freezing logic error`

## 📐 Implementation Rule
- 모든 커밋은 하나의 명세 단위(Blueprint)를 넘지 않는 원자적(Atomic) 단위를 유지한다.
- 커밋 메시지만 보고도 어떤 명세 문서가 업데이트되었는지 추적 가능해야 한다.

## 🚫 Anti-Patterns (Never do this)
1. **Happy Path Bias**: 예외 처리(Error Handling)가 없는 코드는 구현되지 않은 것으로 간주한다.
2. **Library Bloat**: 새로운 패키지 추가 전 반드시 표준 라이브러리로 대체 가능한지 검토한다.
3. **Hard-coded Secrets**: 어떠한 경우에도 코드 내에 민감 정보(Key, PII)를 하드코딩하지 않는다.
4. **Silent Failure**: 에러를 catch하고 아무 작업도 하지 않는(Empty catch block) 행위를 금지한다.

## 🔐 Cross-Language Secret Mapping
각 언어별 표준 설정 파일과 `.env` 환경 변수를 다음과 같이 매핑하여 보안을 유지한다.
- **Java (Spring)**: `application.yml` 내에 `${ENV_VAR_NAME}` 형식을 사용하고, 실제 값은 `.env`를 통해 주입한다.
- **Node.js**: `process.env.ENV_VAR_NAME` 형식을 사용한다.
- **Python**: `os.getenv("ENV_VAR_NAME")` 또는 `pydantic-settings`를 활용한다.

## 🏛 Abstraction & Design Principle
지속 가능한 코드를 위해 모든 레이어에서 적절한 수준의 추상화를 강제한다.

### 1. Frontend Abstraction
- **Logic Decoupling**: 컴포넌트 내부에 복잡한 비즈니스 로직을 방치하지 않는다. 
- **Service Layer**: API 호출 및 데이터 변환 로직은 Custom Hooks 또는 독립된 Service 모듈로 추상화하여 UI와 분리한다.

### 2. Backend Abstraction
- **Infrastructure Agnostic**: 핵심 도메인 로직이 특정 외부 라이브러리나 인프라(DB, Cloud SDK)에 직접 의존하는 것을 금지한다.
- **Interface First**: 인프라 연동 시 반드시 Interface를 먼저 정의하고, 구체적인 구현체는 Adapter 패턴 등을 통해 결합도를 낮춘다.



## ✅ Definition of Done (DOD)
- [ ] 작업 내용이 관련 Blueprint 명세와 일치하는가?
- [ ] 실제 빌드 및 컴파일을 수행하여 에러가 없음을 확인했는가?
- [ ] 주요 비즈니스 로직에 대한 유닛 테스트가 작성 및 통과되었는가?
- [ ] 에러 핸들링 및 로그 메시지가 적절하게 포함되었는가?
- [ ] `ai-spec verify` 명령을 통해 추적성이 확인되었는가?
