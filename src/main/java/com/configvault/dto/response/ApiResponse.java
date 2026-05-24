package com.configvault.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper providing a consistent response structure.
 *
 * <p>All API endpoints return responses wrapped in this class to ensure
 * uniform response formatting across the application. The wrapper includes
 * success/failure status, a human-readable message, the response payload,
 * HTTP status code, and a timestamp.</p>
 *
 * <p>Static factory methods are provided for common response patterns:</p>
 * <ul>
 *     <li>{@link #success(String, Object)} - Successful operation with custom message</li>
 *     <li>{@link #success(Object)} - Successful operation with default message</li>
 *     <li>{@link #error(String, int)} - Error response with status code</li>
 *     <li>{@link #created(String, Object)} - Resource creation response (201)</li>
 * </ul>
 *
 * @param <T> the type of the response data payload
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Indicates whether the operation was successful. */
    private boolean success;

    /** Human-readable message describing the result of the operation. */
    private String message;

    /** The response data payload; may be null for error responses. */
    private T data;

    /** HTTP status code; omitted from JSON if null. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;

    /** ISO-8601 timestamp of when the response was generated; omitted from JSON if null. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String timestamp;

    /**
     * Creates a successful response with a custom message and data payload.
     *
     * @param message descriptive success message
     * @param data    the response payload
     * @param <T>     the type of the payload
     * @return a new {@link ApiResponse} indicating success with HTTP 200
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setStatus(200);
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }

    /**
     * Creates a successful response with a default message and data payload.
     *
     * @param data the response payload
     * @param <T>  the type of the payload
     * @return a new {@link ApiResponse} indicating success with HTTP 200
     */
    public static <T> ApiResponse<T> success(T data) {
        return success("Operation successful", data);
    }

    /**
     * Creates an error response with a message and HTTP status code.
     *
     * @param message descriptive error message
     * @param status  the HTTP status code representing the error
     * @param <T>     the type parameter (data will be null)
     * @return a new {@link ApiResponse} indicating failure
     */
    public static <T> ApiResponse<T> error(String message, int status) {
        ApiResponse<T> response = new ApiResponse<T>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setData(null);
        response.setStatus(status);
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }

    /**
     * Creates a successful resource creation response with HTTP 201 status.
     *
     * @param message descriptive creation message
     * @param data    the newly created resource
     * @param <T>     the type of the payload
     * @return a new {@link ApiResponse} indicating successful creation
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        ApiResponse<T> response = new ApiResponse<T>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setStatus(201);
        response.setTimestamp(LocalDateTime.now().toString());
        return response;
    }
}
