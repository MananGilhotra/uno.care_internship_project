package com.configvault.exception;

import lombok.Getter;

/**
 * Exception thrown when attempting to create a property with a key that already exists.
 *
 * <p>Property keys must be unique in the system. This exception is raised when
 * a create operation detects a key collision with an existing property.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Getter
public class DuplicateKeyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** The duplicate key that caused the conflict. */
    private final String key;

    /**
     * Constructs a new {@code DuplicateKeyException} for the specified key.
     *
     * @param key the property key that already exists
     */
    public DuplicateKeyException(String key) {
        super("Property already exists with key: " + key);
        this.key = key;
    }
}
