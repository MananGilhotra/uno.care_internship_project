package com.configvault.exception;

import com.configvault.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the ConfigVault application.
 *
 * <p>This class intercepts exceptions thrown by controllers and transforms them
 * into consistent {@link ApiResponse} objects with appropriate HTTP status codes.
 * All exceptions are logged at the appropriate level for monitoring and debugging.</p>
 *
 * <p>Handled exception types:</p>
 * <ul>
 *     <li>{@link ResourceNotFoundException} - 404 Not Found</li>
 *     <li>{@link RestrictedKeyException} - 403 Forbidden</li>
 *     <li>{@link DuplicateKeyException} - 409 Conflict</li>
 *     <li>{@link RateLimitExceededException} - 429 Too Many Requests</li>
 *     <li>{@link MethodArgumentNotValidException} - 400 Bad Request (validation)</li>
 *     <li>{@link ConstraintViolationException} - 400 Bad Request (constraints)</li>
 *     <li>{@link HttpRequestMethodNotSupportedException} - 405 Method Not Allowed</li>
 *     <li>{@link Exception} - 500 Internal Server Error (fallback)</li>
 * </ul>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles {@link ResourceNotFoundException} when a requested resource does not exist.
     *
     * @param ex the exception containing resource lookup details
     * @return a 404 response with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link RestrictedKeyException} when an operation is attempted on a restricted key.
     *
     * @param ex the exception containing the restricted key information
     * @return a 403 response with error details
     */
    @ExceptionHandler(RestrictedKeyException.class)
    public ResponseEntity<ApiResponse<?>> handleRestrictedKeyException(RestrictedKeyException ex) {
        log.warn("Restricted key access attempted: {}", ex.getKey());
        ApiResponse<?> response = ApiResponse.error(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles {@link DuplicateKeyException} when a property key already exists.
     *
     * @param ex the exception containing the duplicate key information
     * @return a 409 response with error details
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateKeyException(DuplicateKeyException ex) {
        log.warn("Duplicate key conflict: {}", ex.getKey());
        ApiResponse<?> response = ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles {@link RateLimitExceededException} when the API rate limit is exceeded.
     *
     * @param ex the rate limit exception
     * @return a 429 response with error details
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleRateLimitExceededException(RateLimitExceededException ex) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        ApiResponse<?> response = ApiResponse.error(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS.value());
        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} for Bean Validation failures.
     *
     * <p>Extracts individual field errors and returns them as a map of
     * field name to error message, providing actionable feedback to the client.</p>
     *
     * @param ex the validation exception containing field-level errors
     * @return a 400 response with a map of field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<Map<String, String>> response = new ApiResponse<Map<String, String>>();
        response.setSuccess(false);
        response.setMessage("Validation failed");
        response.setData(fieldErrors);
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ConstraintViolationException} for constraint violations
     * (e.g., path variable or query parameter validation).
     *
     * <p>Extracts individual constraint violations and returns them as a map
     * of property path to violation message.</p>
     *
     * @param ex the constraint violation exception
     * @return a 400 response with constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());

        Map<String, String> violations = new HashMap<String, String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            violations.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        ApiResponse<Map<String, String>> response = new ApiResponse<Map<String, String>>();
        response.setSuccess(false);
        response.setMessage("Constraint violation");
        response.setData(violations);
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link HttpRequestMethodNotSupportedException} when an unsupported
     * HTTP method is used on an endpoint.
     *
     * @param ex the exception containing the unsupported method details
     * @return a 405 response with error details
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMessage());
        String message = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                ex.getMethod(), ex.getSupportedHttpMethods());
        ApiResponse<?> response = ApiResponse.error(message, HttpStatus.METHOD_NOT_ALLOWED.value());
        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Generic fallback handler for all unhandled exceptions.
     *
     * <p>This handler catches any exception not matched by the more specific handlers
     * above. The full stack trace is logged at ERROR level for debugging, while a
     * generic error message is returned to the client to avoid leaking internal details.</p>
     *
     * @param ex the unhandled exception
     * @return a 500 response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.error(
                "An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<ApiResponse<?>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
