package com.configvault.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Centralized application constants for ConfigVault.
 *
 * <p>This utility class holds all constant values used across the application,
 * including masking configuration, pagination defaults, API paths, and
 * standard error messages. All constants are static and immutable.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
public final class AppConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class cannot be instantiated
     */
    private AppConstants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class AppConstants");
    }

    // =========================================================================
    // Security & Masking Constants
    // =========================================================================

    /**
     * Mask value used to hide sensitive property values from API responses.
     */
    public static final String MASK_VALUE = "********";

    /**
     * Set of property keys whose values must be masked in API responses.
     * These keys represent sensitive configuration such as passwords and secrets.
     */
    public static final Set<String> RESTRICTED_KEYS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList(
                    "SWAGGER_PASSWORD",
                    "API_SECRET",
                    "AWS_SECRET_KEY"
            ))
    );

    // =========================================================================
    // Pagination Constants
    // =========================================================================

    /** Default page number for paginated queries (zero-indexed). */
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /** Default number of items per page. */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** Maximum allowed page size to prevent excessive data retrieval. */
    public static final int MAX_PAGE_SIZE = 50;

    // =========================================================================
    // API Path Constants
    // =========================================================================

    /** Base path prefix for all versioned API endpoints. */
    public static final String API_BASE_PATH = "/api/v1";

    // =========================================================================
    // Error Message Constants
    // =========================================================================

    /** Error message template for property not found scenarios. */
    public static final String PROPERTY_NOT_FOUND = "Property not found with key: ";

    /** Error message template for operations on restricted property keys. */
    public static final String PROPERTY_RESTRICTED = "Operation not allowed on restricted key: ";

    /** Error message template for duplicate property key scenarios. */
    public static final String PROPERTY_DUPLICATE = "Property already exists with key: ";
}
