# Blueprint: auth-service

## Overview
`auth-service` handles user registration, login, and JWT token issuance.

## API Specification

### 1. Sign Up
- **Endpoint:** `POST /api/v1/auth/signup`
- **Request:** `SignupRequest` (username, password, email)
- **Response:** `ApiResponse<Void>`

### 2. Login
- **Endpoint:** `POST /api/v1/auth/login`
- **Request:** `LoginRequest` (username, password)
- **Response:** `ApiResponse<JwtTokenResponse>`

## Logic & Security

### 1. JwtTokenProvider
- Responsible for generating, parsing, and validating JWT tokens.
- Contains methods for creating access tokens and refresh tokens.

### 2. SecurityConfig
- Configures Spring Security to permit auth endpoints and secure others.
- Uses `BCryptPasswordEncoder` for password hashing.

### 3. User Domain
- `User` entity with `username`, `password`, `email`, and `roles`.
- `UserRepository` for data access.
