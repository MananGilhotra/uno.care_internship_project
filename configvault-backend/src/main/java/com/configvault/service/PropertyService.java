package com.configvault.service;

import com.configvault.dto.request.PropertyRequest;
import com.configvault.dto.response.PagedResponse;
import com.configvault.dto.response.PropertyResponse;

import java.util.List;

/**
 * Service interface for property management operations.
 *
 * <p>Defines the business contract for creating, reading, updating,
 * and soft-deleting configuration properties. Implementations should
 * handle key normalization, restricted key validation, caching,
 * and transactional boundaries.</p>
 *
 * @author ConfigVault Team
 * @since 1.0.0
 */
public interface PropertyService {

    /**
     * Creates a new property or updates an existing one based on the key.
     *
     * <p>The key is normalized to uppercase and trimmed before processing.
     * Restricted keys will result in a {@code RestrictedKeyException}.
     * If a property with the same key already exists, its value and category
     * are updated; otherwise, a new property is created.</p>
     *
     * @param request the property request containing key, value, and category
     * @return the created or updated property response
     * @throws com.configvault.exception.RestrictedKeyException if the key is restricted
     */
    PropertyResponse createOrUpdateProperty(PropertyRequest request);

    /**
     * Retrieves a property by its key.
     *
     * <p>The key is normalized to uppercase and trimmed. The lookup is
     * case-insensitive. Results are cached for improved performance.</p>
     *
     * @param key the property key to look up
     * @return the property response
     * @throws com.configvault.exception.ResourceNotFoundException if no property is found with the given key
     */
    PropertyResponse getPropertyByKey(String key);

    /**
     * Retrieves a paginated list of all active properties.
     *
     * <p>The page size is validated and clamped to the configured maximum.
     * Results are sorted alphabetically by key.</p>
     *
     * @param page the zero-based page number
     * @param size the number of items per page
     * @return a paginated response of active properties
     */
    PagedResponse<PropertyResponse> getActiveProperties(int page, int size);

    /**
     * Retrieves a paginated list of active properties filtered by category.
     *
     * <p>The category filter is case-insensitive. The page size is validated
     * and clamped to the configured maximum.</p>
     *
     * @param category the category to filter by
     * @param page     the zero-based page number
     * @param size     the number of items per page
     * @return a paginated response of active properties in the given category
     */
    PagedResponse<PropertyResponse> getPropertiesByCategory(String category, int page, int size);

    /**
     * Soft-deletes a property by setting its active status to false.
     *
     * <p>The key is normalized to uppercase and trimmed. Restricted keys
     * cannot be deleted and will result in a {@code RestrictedKeyException}.</p>
     *
     * @param key the property key to soft-delete
     * @return the updated property response with inactive status
     * @throws com.configvault.exception.RestrictedKeyException    if the key is restricted
     * @throws com.configvault.exception.ResourceNotFoundException if no property is found with the given key
     */
    PropertyResponse softDeleteProperty(String key);

    /**
     * Retrieves all distinct categories from active properties.
     *
     * <p>Results are cached and sorted alphabetically.</p>
     *
     * @return a list of distinct active category names
     */
    List<String> getActiveCategories();
}
