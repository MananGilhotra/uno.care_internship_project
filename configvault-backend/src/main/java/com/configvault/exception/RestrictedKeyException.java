package com.configvault.exception;

import lombok.Getter;

/**
 * Exception thrown when an operation is attempted on a restricted property key.
 *
 * <p>Restricted keys represent sensitive configuration values (e.g., passwords,
 * secrets) that cannot be modified or deleted through the standard API to
 * prevent accidental exposure or loss of critical configuration.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Getter
public class RestrictedKeyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** The restricted key that the operation was attempted on. */
    private final String key;

    /**
     * Constructs a new {@code RestrictedKeyException} for the specified key.
     *
     * @param key the restricted property key
     */
    public RestrictedKeyException(String key) {
        super("Operation not allowed on restricted key: " + key);
        this.key = key;
    }
}
