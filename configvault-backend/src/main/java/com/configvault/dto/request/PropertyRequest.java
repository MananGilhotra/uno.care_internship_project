package com.configvault.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for incoming property creation and update requests.
 *
 * <p>This DTO encapsulates the client-supplied data needed to create or update
 * a configuration property. All fields are validated using Bean Validation
 * constraints to ensure data integrity before reaching the service layer.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyRequest {

    /**
     * The unique key identifier for the property.
     * Will be normalized to uppercase during persistence.
     */
    @NotBlank(message = "Property key is required")
    @Size(min = 1, max = 255, message = "Key must be between 1 and 255 characters")
    private String key;

    /**
     * The value to associate with the property key.
     */
    @NotBlank(message = "Property value is required")
    @Size(min = 1, max = 1000, message = "Value must be between 1 and 1000 characters")
    private String value;

    /**
     * Optional category for organizing properties (e.g., DATABASE, SECURITY, APPLICATION).
     */
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;
}
