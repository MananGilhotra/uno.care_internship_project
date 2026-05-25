package com.configvault.exception;

/**
 * Exception thrown when a client exceeds the configured API rate limit.
 *
 * <p>Rate limiting is enforced to protect the API from abuse and ensure
 * fair resource usage. When this exception is thrown, the client should
 * wait before making additional requests.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
public class RateLimitExceededException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code RateLimitExceededException} with a default message.
     */
    public RateLimitExceededException() {
        super("Rate limit exceeded. Please try again later.");
    }

    /**
     * Constructs a new {@code RateLimitExceededException} with a custom message.
     *
     * @param message the detail message
     */
    public RateLimitExceededException(String message) {
        super(message);
    }
}
