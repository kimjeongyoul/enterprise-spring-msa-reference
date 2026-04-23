# ADR 018: Metadata Management via Common Service

## Context
Hardcoding common codes (status, category, etc.) or i18n messages in `common-lib` or individual services creates deployment dependencies. To achieve true independent deployability, we need a centralized service that manages these "data-like" codes.

## Decision
We will introduce `common-service` as a dedicated microservice for Metadata Management.

1. **Responsibilities of `common-service`:**
    - Serve as the Single Source of Truth for Common Codes (Group/Detail structure).
    - Manage i18n messages in a database (optional/future).
    - Provide REST APIs for other services to fetch metadata.

2. **Communication Strategy:**
    - **OpenFeign:** Services will call `common-service` to fetch codes.
    - **Caching:** To avoid performance bottlenecks, consuming services will use a Local Cache (or Redis) and refresh it periodically or via events.

3. **`common-lib` Role:**
    - Provides the `CommonCode` DTO and `CommonCodeClient` interface (Contract).
    - Does not contain the actual data.

## Consequences
- **Dynamic Configuration:** Codes can be added/modified via DB without any service redeployment.
- **Consistency:** All services use the same definitions for shared concepts (e.g., Order Status).
- **Service Dependency:** `common-service` becomes a critical infrastructure component (requires high availability).
