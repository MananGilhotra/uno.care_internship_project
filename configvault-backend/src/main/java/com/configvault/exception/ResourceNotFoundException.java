package com.configvault.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 *
 * <p>This exception carries contextual information about the resource type,
 * the field used for lookup, and the value that was searched for, enabling
 * informative error messages in API responses.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** The name of the resource type that was not found (e.g., "Property"). */
    private final String resourceName;

    /** The field name used for the lookup (e.g., "key"). */
    private final String fieldName;

    /** The value that was searched for but not found. */
    private final String fieldValue;

    /**
     * Constructs a new {@code ResourceNotFoundException} with detailed context.
     *
     * @param resourceName the type of resource that was not found
     * @param fieldName    the field used in the lookup
     * @param fieldValue   the value that was searched for
     */
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
