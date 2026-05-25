package com.configvault.service;

import com.configvault.constants.AppConstants;
import com.configvault.dto.request.PropertyRequest;
import com.configvault.dto.response.PagedResponse;
import com.configvault.dto.response.PropertyResponse;
import com.configvault.entity.Property;
import com.configvault.exception.ResourceNotFoundException;
import com.configvault.exception.RestrictedKeyException;
import com.configvault.mapper.PropertyMapper;
import com.configvault.repository.PropertyRepository;
import com.configvault.service.impl.PropertyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PropertyServiceImpl}.
 * Uses Mockito to mock dependencies and verify service-layer business logic.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"null", "unchecked"})
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyMapper propertyMapper;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private Property property;
    private PropertyRequest propertyRequest;
    private PropertyResponse propertyResponse;

    @BeforeEach
    void setUp() {
        property = new Property();
        property.setId(1L);
        property.setKey("APP_NAME");
        property.setValue("ConfigVault");
        property.setCategory("APPLICATION");
        property.setIsActive(true);
        property.setCreatedDate(LocalDateTime.now());
        property.setLastModifiedDate(LocalDateTime.now());

        propertyRequest = new PropertyRequest();
        propertyRequest.setKey("APP_NAME");
        propertyRequest.setValue("ConfigVault");
        propertyRequest.setCategory("APPLICATION");

        propertyResponse = new PropertyResponse();
        propertyResponse.setId(1L);
        propertyResponse.setKey("APP_NAME");
        propertyResponse.setValue("ConfigVault");
        propertyResponse.setCategory("APPLICATION");
        propertyResponse.setIsActive(true);
        propertyResponse.setIsRestricted(false);
        propertyResponse.setCreatedDate(LocalDateTime.now());
        propertyResponse.setLastModifiedDate(LocalDateTime.now());
    }

    // ========================================================================
    // CREATE / UPDATE PROPERTY
    // ========================================================================

    @Test
    @DisplayName("Should create a new property successfully when key does not exist")
    void createProperty_Success() {
        // Given
        when(propertyRepository.findByKeyIgnoreCase("APP_NAME")).thenReturn(Optional.empty());
        when(propertyMapper.toEntity(propertyRequest)).thenReturn(property);
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(propertyMapper.toResponse(property)).thenReturn(propertyResponse);

        // When
        PropertyResponse result = propertyService.createOrUpdateProperty(propertyRequest);

        // Then
        assertNotNull(result);
        assertEquals("APP_NAME", result.getKey());
        assertEquals("ConfigVault", result.getValue());
        verify(propertyRepository).findByKeyIgnoreCase("APP_NAME");
        verify(propertyRepository).save(any(Property.class));
        verify(propertyMapper).toResponse(property);
    }

    @Test
    @DisplayName("Should update an existing property successfully when key already exists")
    void updateProperty_Success() {
        // Given
        when(propertyRepository.findByKeyIgnoreCase("APP_NAME")).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(propertyMapper.toResponse(property)).thenReturn(propertyResponse);

        PropertyRequest updateRequest = new PropertyRequest();
        updateRequest.setKey("APP_NAME");
        updateRequest.setValue("ConfigVault-Updated");
        updateRequest.setCategory("APPLICATION");

        // When
        PropertyResponse result = propertyService.createOrUpdateProperty(updateRequest);

        // Then
        assertNotNull(result);
        verify(propertyRepository).findByKeyIgnoreCase("APP_NAME");
        verify(propertyRepository).save(any(Property.class));
        verify(propertyMapper, never()).toEntity(any(PropertyRequest.class));
    }

    @Test
    @DisplayName("Should throw RestrictedKeyException when creating property with restricted key")
    void createProperty_RestrictedKey_ThrowsException() {
        // Given
        PropertyRequest restrictedRequest = new PropertyRequest();
        restrictedRequest.setKey("SWAGGER_PASSWORD");
        restrictedRequest.setValue("secret123");
        restrictedRequest.setCategory("SECURITY");

        // When & Then
        assertThrows(RestrictedKeyException.class, () ->
                propertyService.createOrUpdateProperty(restrictedRequest));

        verify(propertyRepository, never()).save(any(Property.class));
    }

    // ========================================================================
    // GET PROPERTY BY KEY
    // ========================================================================

    @Test
    @DisplayName("Should return property when found by key")
    void getPropertyByKey_Success() {
        // Given
        when(propertyRepository.findByKeyIgnoreCase("APP_NAME")).thenReturn(Optional.of(property));
        when(propertyMapper.toResponse(property)).thenReturn(propertyResponse);

        // When
        PropertyResponse result = propertyService.getPropertyByKey("APP_NAME");

        // Then
        assertNotNull(result);
        assertEquals("APP_NAME", result.getKey());
        assertEquals("ConfigVault", result.getValue());
        verify(propertyRepository).findByKeyIgnoreCase("APP_NAME");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when property key does not exist")
    void getPropertyByKey_NotFound_ThrowsException() {
        // Given
        when(propertyRepository.findByKeyIgnoreCase("NON_EXISTENT")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                propertyService.getPropertyByKey("NON_EXISTENT"));

        verify(propertyRepository).findByKeyIgnoreCase("NON_EXISTENT");
    }

    @Test
    @DisplayName("Should return masked value when querying a restricted key")
    void getPropertyByKey_MaskedValue() {
        // Given
        Property restrictedProperty = new Property();
        restrictedProperty.setId(2L);
        restrictedProperty.setKey("JWT_SECRET");
        restrictedProperty.setValue("super-secret-jwt-key");
        restrictedProperty.setCategory("SECURITY");
        restrictedProperty.setIsActive(true);
        restrictedProperty.setCreatedDate(LocalDateTime.now());
        restrictedProperty.setLastModifiedDate(LocalDateTime.now());

        PropertyResponse maskedResponse = new PropertyResponse();
        maskedResponse.setId(2L);
        maskedResponse.setKey("JWT_SECRET");
        maskedResponse.setValue(AppConstants.MASK_VALUE);
        maskedResponse.setCategory("SECURITY");
        maskedResponse.setIsActive(true);
        maskedResponse.setIsRestricted(true);

        when(propertyRepository.findByKeyIgnoreCase("JWT_SECRET")).thenReturn(Optional.of(restrictedProperty));
        when(propertyMapper.toResponse(restrictedProperty)).thenReturn(maskedResponse);

        // When
        PropertyResponse result = propertyService.getPropertyByKey("JWT_SECRET");

        // Then
        assertNotNull(result);
        assertEquals(AppConstants.MASK_VALUE, result.getValue());
        assertTrue(result.getIsRestricted());
    }

    // ========================================================================
    // GET ACTIVE PROPERTIES (PAGINATED)
    // ========================================================================

    @Test
    @DisplayName("Should return paginated list of active properties")
    void getActiveProperties_Success() {
        // Given
        List<Property> properties = Arrays.asList(property);
        Page<Property> propertyPage = new PageImpl<>(properties, PageRequest.of(0, 10), 1);

        PagedResponse<PropertyResponse> expectedPagedResponse = PagedResponse.<PropertyResponse>builder()
                .content(Collections.singletonList(propertyResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(propertyRepository.findByIsActiveTrue(any(Pageable.class))).thenReturn(propertyPage);
        when(propertyMapper.toPagedResponse(any(Page.class))).thenReturn(expectedPagedResponse);

        // When
        PagedResponse<PropertyResponse> result = propertyService.getActiveProperties(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isLast());
        verify(propertyRepository).findByIsActiveTrue(any(Pageable.class));
    }

    // ========================================================================
    // GET PROPERTIES BY CATEGORY (PAGINATED)
    // ========================================================================

    @Test
    @DisplayName("Should return paginated list of properties filtered by category")
    void getPropertiesByCategory_Success() {
        // Given
        List<Property> properties = Collections.singletonList(property);
        Page<Property> propertyPage = new PageImpl<>(properties, PageRequest.of(0, 10), 1);

        PagedResponse<PropertyResponse> expectedPagedResponse = PagedResponse.<PropertyResponse>builder()
                .content(Collections.singletonList(propertyResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(propertyRepository.findByCategoryIgnoreCaseAndIsActiveTrue(eq("APPLICATION"), any(Pageable.class)))
                .thenReturn(propertyPage);
        when(propertyMapper.toPagedResponse(any(Page.class))).thenReturn(expectedPagedResponse);

        // When
        PagedResponse<PropertyResponse> result = propertyService.getPropertiesByCategory("APPLICATION", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("APPLICATION", result.getContent().get(0).getCategory());
        verify(propertyRepository).findByCategoryIgnoreCaseAndIsActiveTrue(eq("APPLICATION"), any(Pageable.class));
    }

    // ========================================================================
    // SOFT DELETE PROPERTY
    // ========================================================================

    @Test
    @DisplayName("Should soft-delete property by setting isActive to false")
    void softDeleteProperty_Success() {
        // Given
        when(propertyRepository.findByKeyIgnoreCase("APP_NAME")).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(propertyMapper.toResponse(any(Property.class))).thenReturn(propertyResponse);

        // When
        PropertyResponse result = propertyService.softDeleteProperty("APP_NAME");

        // Then
        assertNotNull(result);
        assertFalse(property.getIsActive());
        verify(propertyRepository).findByKeyIgnoreCase("APP_NAME");
        verify(propertyRepository).save(property);
    }

    @Test
    @DisplayName("Should throw RestrictedKeyException when trying to soft-delete a restricted key")
    void softDeleteProperty_RestrictedKey_ThrowsException() {
        // When & Then
        assertThrows(RestrictedKeyException.class, () ->
                propertyService.softDeleteProperty("JWT_SECRET"));

        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when soft-deleting a non-existent key")
    void softDeleteProperty_NotFound_ThrowsException() {
        // Given
        when(propertyRepository.findByKeyIgnoreCase("MISSING_KEY")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                propertyService.softDeleteProperty("MISSING_KEY"));

        verify(propertyRepository, never()).save(any(Property.class));
    }

    // ========================================================================
    // GET ACTIVE CATEGORIES
    // ========================================================================

    @Test
    @DisplayName("Should return distinct list of active categories")
    void getActiveCategories_Success() {
        // Given
        List<String> categories = Arrays.asList("APPLICATION", "SECURITY", "DATABASE");
        when(propertyRepository.findDistinctActiveCategories()).thenReturn(categories);

        // When
        List<String> result = propertyService.getActiveCategories();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("APPLICATION"));
        assertTrue(result.contains("SECURITY"));
        assertTrue(result.contains("DATABASE"));
        verify(propertyRepository).findDistinctActiveCategories();
    }
}
