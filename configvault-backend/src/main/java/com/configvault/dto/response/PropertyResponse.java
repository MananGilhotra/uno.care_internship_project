package com.configvault.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for outgoing property responses.
 *
 * <p>This DTO represents a configuration property as returned to API clients.
 * Sensitive property values are automatically masked based on the property key,
 * and the {@code isRestricted} flag indicates whether the value has been masked.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {

    /** Unique identifier of the property. */
    private Long id;

    /** The property key (always uppercase). */
    private String key;

    /** The property value, or a masked placeholder if the key is restricted. */
    private String value;

    /** The category this property belongs to. */
    private String category;

    /** Whether the property is currently active. */
    private Boolean isActive;

    /** Timestamp of when the property was first created. */
    private LocalDateTime createdDate;

    /** Timestamp of the most recent modification. */
    private LocalDateTime lastModifiedDate;

    /** Flag indicating whether this property's value has been masked for security. */
    private Boolean isRestricted;
}
