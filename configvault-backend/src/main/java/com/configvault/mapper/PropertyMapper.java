package com.configvault.mapper;

import com.configvault.dto.request.PropertyRequest;
import com.configvault.dto.response.PagedResponse;
import com.configvault.dto.response.PropertyResponse;
import com.configvault.entity.Property;
import com.configvault.util.MaskingUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual mapper component for converting between Property entities and DTOs.
 *
 * <p>This mapper handles the transformation between the persistence layer
 * ({@link Property} entities) and the API layer ({@link PropertyRequest}
 * and {@link PropertyResponse} DTOs). It also integrates value masking
 * for restricted keys via {@link MaskingUtil}.</p>
 *
 * <p>This is implemented as a Spring {@link Component} rather than using
 * MapStruct to maintain simplicity and full Java 8 compatibility.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Component
public class PropertyMapper {

    /**
     * Converts a {@link Property} entity to a {@link PropertyResponse} DTO.
     *
     * <p>During conversion, the property value is automatically masked if
     * the key is classified as restricted. The {@code isRestricted} flag
     * in the response indicates whether masking was applied.</p>
     *
     * @param property the entity to convert; must not be null
     * @return the corresponding response DTO with masked values if applicable
     */
    public PropertyResponse toResponse(Property property) {
        boolean restricted = MaskingUtil.isRestrictedKey(property.getKey());
        return PropertyResponse.builder()
                .id(property.getId())
                .key(property.getKey())
                .value(MaskingUtil.maskValue(property.getKey(), property.getValue()))
                .category(property.getCategory())
                .isActive(property.getIsActive())
                .createdDate(property.getCreatedDate())
                .lastModifiedDate(property.getLastModifiedDate())
                .isRestricted(restricted)
                .build();
    }

    /**
     * Converts a {@link PropertyRequest} DTO to a new {@link Property} entity.
     *
     * <p>The property key is normalized to uppercase during conversion.
     * The entity is created with default values for {@code isActive} (true)
     * and audit timestamps (managed by Hibernate).</p>
     *
     * @param request the incoming request DTO; must not be null
     * @return a new entity populated from the request data
     */
    public Property toEntity(PropertyRequest request) {
        return Property.builder()
                .key(request.getKey().toUpperCase().trim())
                .value(request.getValue())
                .category(request.getCategory())
                .build();
    }

    /**
     * Updates an existing {@link Property} entity with values from a {@link PropertyRequest}.
     *
     * <p>Only the key, value, and category fields are updated. The key is
     * normalized to uppercase. Audit timestamps and the active flag are
     * not modified by this method.</p>
     *
     * @param property the existing entity to update; must not be null
     * @param request  the request containing updated values; must not be null
     */
    public void updateEntity(Property property, PropertyRequest request) {
        property.setKey(request.getKey().toUpperCase().trim());
        property.setValue(request.getValue());
        property.setCategory(request.getCategory());
    }

    /**
     * Converts a Spring Data {@link Page} of {@link Property} entities to a
     * {@link PagedResponse} of {@link PropertyResponse} DTOs.
     *
     * <p>Each entity in the page is individually mapped using {@link #toResponse(Property)},
     * ensuring value masking is applied consistently.</p>
     *
     * @param page the Spring Data page of property entities
     * @return a paginated response DTO containing mapped property responses
     */
    public PagedResponse<PropertyResponse> toPagedResponse(Page<Property> page) {
        List<PropertyResponse> responses = new ArrayList<PropertyResponse>();
        for (Property property : page.getContent()) {
            responses.add(toResponse(property));
        }

        return PagedResponse.<PropertyResponse>builder()
                .content(responses)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
