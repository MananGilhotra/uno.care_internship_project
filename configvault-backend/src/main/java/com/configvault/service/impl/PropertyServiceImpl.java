package com.configvault.service.impl;

import com.configvault.constants.AppConstants;
import com.configvault.dto.request.PropertyRequest;
import com.configvault.dto.response.PagedResponse;
import com.configvault.dto.response.PropertyResponse;
import com.configvault.entity.Property;
import com.configvault.exception.ResourceNotFoundException;
import com.configvault.exception.RestrictedKeyException;
import com.configvault.mapper.PropertyMapper;
import com.configvault.repository.PropertyRepository;
import com.configvault.service.PropertyService;
import com.configvault.util.MaskingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link PropertyService} providing business logic for
 * configuration property management.
 *
 * <p>This service handles key normalization, restricted key validation,
 * caching via Spring Cache abstraction, and transactional boundaries.
 * All read operations default to read-only transactions for performance
 * optimization.</p>
 *
 * @author ConfigVault Team
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    /**
     * {@inheritDoc}
     *
     * <p>Normalizes the key to uppercase, validates against restricted keys,
     * then either updates the existing property or creates a new one.
     * Evicts all entries from the "properties" cache on successful mutation.</p>
     */
    @Override
    @Transactional
    @CacheEvict(value = "properties", allEntries = true)
    public PropertyResponse createOrUpdateProperty(PropertyRequest request) {
        String normalizedKey = request.getKey().trim().toUpperCase();
        log.info("Processing create/update request for property key: '{}'", normalizedKey);

        if (MaskingUtil.isRestrictedKey(normalizedKey)) {
            log.warn("Attempt to create/update restricted key: '{}'", normalizedKey);
            throw new RestrictedKeyException(normalizedKey);
        }

        Optional<Property> existingProperty = propertyRepository.findByKeyIgnoreCase(normalizedKey);

        if (existingProperty.isPresent()) {
            Property property = existingProperty.get();
            log.info("Property with key '{}' already exists (id={}). Updating value and category.",
                    normalizedKey, property.getId());
            propertyMapper.updateEntity(property, request);
            Property savedProperty = propertyRepository.save(property);
            log.info("Successfully updated property with key '{}' (id={})", normalizedKey, savedProperty.getId());
            return propertyMapper.toResponse(savedProperty);
        }

        log.info("Creating new property with key '{}'", normalizedKey);
        Property newProperty = propertyMapper.toEntity(request);
        Property savedProperty = propertyRepository.save(newProperty);
        log.info("Successfully created property with key '{}' (id={})", normalizedKey, savedProperty.getId());
        return propertyMapper.toResponse(savedProperty);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Normalizes the key and performs a case-insensitive lookup.
     * Results are cached using the uppercase key as the cache key.</p>
     */
    @Override
    @Cacheable(value = "properties", key = "#key.toUpperCase()")
    public PropertyResponse getPropertyByKey(String key) {
        String normalizedKey = key.trim().toUpperCase();
        log.info("Retrieving property by key: '{}'", normalizedKey);

        Property property = propertyRepository.findByKeyIgnoreCase(normalizedKey)
                .orElseThrow(() -> {
                    log.warn("Property not found with key: '{}'", normalizedKey);
                    return new ResourceNotFoundException("Property", "key", normalizedKey);
                });

        log.debug("Successfully retrieved property with key '{}' (id={})", normalizedKey, property.getId());
        return propertyMapper.toResponse(property);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates and clamps the page size to the configured maximum,
     * then retrieves active properties sorted alphabetically by key.</p>
     */
    @Override
    public PagedResponse<PropertyResponse> getActiveProperties(int page, int size) {
        int validatedSize = validatePageSize(size);
        log.info("Retrieving active properties - page: {}, size: {}", page, validatedSize);

        Page<Property> propertyPage = propertyRepository.findByIsActiveTrue(
                PageRequest.of(page, validatedSize, Sort.by(Sort.Direction.ASC, "key"))
        );

        log.debug("Found {} active properties (total: {}, pages: {})",
                propertyPage.getNumberOfElements(),
                propertyPage.getTotalElements(),
                propertyPage.getTotalPages());

        return propertyMapper.toPagedResponse(propertyPage);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates and clamps the page size, then performs a case-insensitive
     * category filter on active properties.</p>
     */
    @Override
    public PagedResponse<PropertyResponse> getPropertiesByCategory(String category, int page, int size) {
        int validatedSize = validatePageSize(size);
        log.info("Retrieving properties by category '{}' - page: {}, size: {}", category, page, validatedSize);

        Page<Property> propertyPage = propertyRepository.findByCategoryIgnoreCaseAndIsActiveTrue(
                category,
                PageRequest.of(page, validatedSize, Sort.by(Sort.Direction.ASC, "key"))
        );

        log.debug("Found {} properties in category '{}' (total: {}, pages: {})",
                propertyPage.getNumberOfElements(),
                category,
                propertyPage.getTotalElements(),
                propertyPage.getTotalPages());

        return propertyMapper.toPagedResponse(propertyPage);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Normalizes the key, validates against restricted keys, locates the
     * property, sets it to inactive, and persists the change. Evicts all
     * entries from the "properties" cache.</p>
     */
    @Override
    @Transactional
    @CacheEvict(value = "properties", allEntries = true)
    public PropertyResponse softDeleteProperty(String key) {
        String normalizedKey = key.trim().toUpperCase();
        log.info("Processing soft-delete request for property key: '{}'", normalizedKey);

        if (MaskingUtil.isRestrictedKey(normalizedKey)) {
            log.warn("Attempt to soft-delete restricted key: '{}'", normalizedKey);
            throw new RestrictedKeyException(normalizedKey);
        }

        Property property = propertyRepository.findByKeyIgnoreCase(normalizedKey)
                .orElseThrow(() -> {
                    log.warn("Property not found for soft-delete with key: '{}'", normalizedKey);
                    return new ResourceNotFoundException("Property", "key", normalizedKey);
                });

        property.setIsActive(false);
        Property savedProperty = propertyRepository.save(property);
        log.info("Successfully soft-deleted property with key '{}' (id={})", normalizedKey, savedProperty.getId());

        return propertyMapper.toResponse(savedProperty);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves distinct categories from all active properties.
     * Results are cached under the "categories" cache.</p>
     */
    @Override
    @Cacheable(value = "categories")
    public List<String> getActiveCategories() {
        log.info("Retrieving all active categories");
        List<String> categories = propertyRepository.findDistinctActiveCategories();
        log.debug("Found {} active categories", categories.size());
        return categories;
    }

    /**
     * Validates and clamps the page size to ensure it does not exceed the
     * configured maximum.
     *
     * @param size the requested page size
     * @return the validated page size, clamped to {@link AppConstants#MAX_PAGE_SIZE}
     */
    private int validatePageSize(int size) {
        if (size > AppConstants.MAX_PAGE_SIZE) {
            log.warn("Requested page size {} exceeds maximum {}. Clamping to maximum.",
                    size, AppConstants.MAX_PAGE_SIZE);
            return AppConstants.MAX_PAGE_SIZE;
        }
        return size;
    }
}
