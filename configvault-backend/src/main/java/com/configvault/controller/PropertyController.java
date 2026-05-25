package com.configvault.controller;

import com.configvault.constants.AppConstants;
import com.configvault.dto.request.PropertyRequest;
import com.configvault.dto.response.ApiResponse;
import com.configvault.dto.response.PagedResponse;
import com.configvault.dto.response.PropertyResponse;
import com.configvault.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing configuration properties.
 *
 * <p>Provides endpoints for creating, retrieving, filtering, and
 * soft-deleting configuration properties. All endpoints are prefixed
 * with the application's base API path followed by "/properties".</p>
 *
 * @author ConfigVault Team
 * @since 1.0.0
 */
@RestController
@RequestMapping(AppConstants.API_BASE_PATH + "/properties")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Property Management", description = "APIs for managing configuration properties")
public class PropertyController {

    private final PropertyService propertyService;

    /**
     * Creates a new property or updates an existing one.
     *
     * <p>If a property with the same key already exists, its value and
     * category are updated. Otherwise, a new property is created.</p>
     *
     * @param request the property request payload containing key, value, and category
     * @return a 201 Created response with the created or updated property
     */
    @PostMapping
    @Operation(summary = "Create or update a property",
            description = "Creates a new property or updates an existing one based on the key")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Property created/updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Restricted key operation")
    })
    public ResponseEntity<ApiResponse<PropertyResponse>> createOrUpdateProperty(
            @Valid @RequestBody PropertyRequest request) {
        log.info("REST request to create/update property with key: '{}'", request.getKey());

        PropertyResponse result = propertyService.createOrUpdateProperty(request);

        log.info("Successfully processed create/update for property key: '{}'", request.getKey());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Property created/updated successfully", result));
    }

    /**
     * Retrieves a property by its key.
     *
     * @param key the property key to look up
     * @return a 200 OK response with the property details
     */
    @GetMapping("/{key}")
    @Operation(summary = "Get property by key",
            description = "Retrieves a single property by its unique key (case-insensitive)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Property found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiResponse<PropertyResponse>> getPropertyByKey(
            @Parameter(description = "The property key", required = true)
            @PathVariable String key) {
        log.info("REST request to get property by key: '{}'", key);

        PropertyResponse result = propertyService.getPropertyByKey(key);

        log.debug("Successfully retrieved property with key: '{}'", key);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Lists all active properties with pagination support.
     *
     * @param page the zero-based page number (default: 0)
     * @param size the number of items per page (default: 10)
     * @return a 200 OK response with a paginated list of active properties
     */
    @GetMapping
    @Operation(summary = "List all active properties",
            description = "Retrieves a paginated list of all active configuration properties")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Properties retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PagedResponse<PropertyResponse>>> getActiveProperties(
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to list active properties - page: {}, size: {}", page, size);

        PagedResponse<PropertyResponse> result = propertyService.getActiveProperties(page, size);

        log.debug("Successfully retrieved {} active properties", result.getContent().size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Filters active properties by category with pagination support.
     *
     * @param category the category to filter by (case-insensitive)
     * @param page     the zero-based page number (default: 0)
     * @param size     the number of items per page (default: 10)
     * @return a 200 OK response with a paginated list of properties in the given category
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Filter properties by category",
            description = "Retrieves a paginated list of active properties filtered by category")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Properties retrieved successfully")
    })
    public ResponseEntity<ApiResponse<PagedResponse<PropertyResponse>>> getPropertiesByCategory(
            @Parameter(description = "The category name", required = true)
            @PathVariable String category,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get properties by category '{}' - page: {}, size: {}", category, page, size);

        PagedResponse<PropertyResponse> result = propertyService.getPropertiesByCategory(category, page, size);

        log.debug("Successfully retrieved {} properties in category '{}'", result.getContent().size(), category);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Soft-deletes a property by setting its active status to false.
     *
     * @param key the property key to soft-delete
     * @return a 200 OK response with the deactivated property
     */
    @DeleteMapping("/{key}")
    @Operation(summary = "Soft delete a property",
            description = "Marks a property as inactive (soft delete) by its key")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Property soft-deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Restricted key operation"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<ApiResponse<PropertyResponse>> softDeleteProperty(
            @Parameter(description = "The property key to delete", required = true)
            @PathVariable String key) {
        log.info("REST request to soft-delete property with key: '{}'", key);

        PropertyResponse result = propertyService.softDeleteProperty(key);

        log.info("Successfully soft-deleted property with key: '{}'", key);
        return ResponseEntity.ok(ApiResponse.success("Property soft-deleted successfully", result));
    }

    /**
     * Retrieves all distinct categories from active properties.
     *
     * @return a 200 OK response with a list of active category names
     */
    @GetMapping("/categories")
    @Operation(summary = "Get all active categories",
            description = "Retrieves a sorted list of all distinct categories from active properties")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<String>>> getActiveCategories() {
        log.info("REST request to get all active categories");

        List<String> categories = propertyService.getActiveCategories();

        log.debug("Successfully retrieved {} active categories", categories.size());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
