# Blueprint: gateway-service

## Overview
`gateway-service` acts as the entry point for all client requests, providing routing and centralized authentication.

## Routing Rules
Configured in `application.yml`:
- Path `/auth/**` routes to `auth-service`.
- Path `/api/**` (non-auth) routes to respective services.

## JwtAuthenticationFilter
A Global Filter that intercepts incoming requests:
1. Skips authentication for public paths (e.g., `/auth/login`, `/auth/signup`).
2. Extracts the `Authorization` header.
3. Validates the JWT token using a shared secret or by calling an internal validation logic (here, we'll implement local validation if secret is shared, or simple header check).
4. Propagates user information via headers to downstream services.
