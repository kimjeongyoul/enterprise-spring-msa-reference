# ADR 017: Thin Common Library & Resource Isolation

## Context
As the number of microservices increases, we've identified that `common-lib` often becomes a bottleneck. When `common-lib` contains frequently changing content (like i18n messages or domain constants), any update requires all dependent services to be rebuilt and redeployed. This violates the principle of **Independent Deployability** and creates a "Distributed Monolith".

## Decision
We will shift to a "Thin Common Library" model where `common-lib` only defines **Protocols, Conventions, and Infrastructure SDKs**, not **Content or Data**.

1. **Protocol & Convention (Keep in `common-lib`):**
    - Standard API Response/Page structures.
    - Global Exception Handling logic.
    - Infrastructure configurations (Jackson, Logging filters).
    - Interface definitions (MessagePublisher, StorageService).

2. **Content & Data (Move out of `common-lib`):**
    - `messages.properties` (i18n): Move to individual services or an external Config Server.
    - Domain-specific constants or enums that change frequently.
    - Concrete error message strings.

3. **Implementation Strategy:**
    - `I18nConfig` in `common-lib` will provide a default `MessageSource` bean, but it will be configured to look for messages in the consuming service's classpath.
    - Remove global `messages.properties` from `common-lib`.

## Consequences
- **Improved Deployment Autonomy:** Services no longer need to redeploy when a simple translation or non-critical constant in another service changes.
- **Clearer Boundaries:** `common-lib` becomes a set of rules (How we communicate) rather than a shared database of strings.
- **Slight Overhead:** Each service must now manage its own message files (until we implement a centralized Config Server).
