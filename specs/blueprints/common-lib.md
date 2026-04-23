# Blueprint: common-lib

## Overview
`common-lib` provides shared response structures and error handling mechanisms for all microservices in the system.

## Core Components

### 1. ApiResponse<T>
A standard wrapper for all API responses to ensure consistency across services.

- `success`: boolean - Indicates if the request was successful.
- `data`: T - The actual payload of the response.
- `error`: ErrorResponse - Details about the error if success is false.

### 2. ErrorCode (Enum)
Centralized definition of application-specific error codes and messages.

- `COMMON_ERROR`: Generic error code.
- `AUTH_FAILED`: Authentication failure.
- `USER_NOT_FOUND`: Resource not found.
- `INVALID_INPUT`: Validation error.

### 3. GlobalExceptionHandler
A central exception handler using `@RestControllerAdvice` to catch and format exceptions into the standard `ApiResponse` format.
