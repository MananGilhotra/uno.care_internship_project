package com.configvault.repository;

import com.configvault.entity.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link PropertyRepository}.
 * Uses an embedded H2 database via {@link DataJpaTest} to verify
 * custom query methods execute correctly against a real data store.
 */
@DataJpaTest
@ActiveProfiles("test")
class PropertyRepositoryTest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Property activeProperty;
    private Property inactiveProperty;
    private Property securityProperty;

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
        entityManager.flush();

        activeProperty = new Property();
        activeProperty.setKey("APP_NAME");
        activeProperty.setValue("ConfigVault");
        activeProperty.setCategory("APPLICATION");
        activeProperty.setIsActive(true);
        activeProperty.setCreatedDate(LocalDateTime.now());
        activeProperty.setLastModifiedDate(LocalDateTime.now());
        entityManager.persistAndFlush(activeProperty);

        inactiveProperty = new Property();
        inactiveProperty.setKey("OLD_SETTING");
        inactiveProperty.setValue("deprecated");
        inactiveProperty.setCategory("APPLICATION");
        inactiveProperty.setIsActive(false);
        inactiveProperty.setCreatedDate(LocalDateTime.now());
        inactiveProperty.setLastModifiedDate(LocalDateTime.now());
        entityManager.persistAndFlush(inactiveProperty);

        securityProperty = new Property();
        securityProperty.setKey("API_KEY");
        securityProperty.setValue("abc-123");
        securityProperty.setCategory("SECURITY");
        securityProperty.setIsActive(true);
        securityProperty.setCreatedDate(LocalDateTime.now());
        securityProperty.setLastModifiedDate(LocalDateTime.now());
        entityManager.persistAndFlush(securityProperty);

        entityManager.clear();
    }

    // ========================================================================
    // findByKeyIgnoreCase
    // ========================================================================

    @Test
    @DisplayName("Should find property by key ignoring case")
    void findByKeyIgnoreCase_Found() {
        // When
        Optional<Property> result = propertyRepository.findByKeyIgnoreCase("app_name");

        // Then
        assertTrue(result.isPresent());
        assertEquals("APP_NAME", result.get().getKey());
        assertEquals("ConfigVault", result.get().getValue());
    }

    @Test
    @DisplayName("Should return empty Optional when key does not exist")
    void findByKeyIgnoreCase_NotFound() {
        // When
        Optional<Property> result = propertyRepository.findByKeyIgnoreCase("NON_EXISTENT_KEY");

        // Then
        assertFalse(result.isPresent());
    }

    // ========================================================================
    // findByIsActiveTrue
    // ========================================================================

    @Test
    @DisplayName("Should return only active properties")
    void findByIsActiveTrue_ReturnsActiveOnly() {
        // When
        Page<Property> activePage = propertyRepository.findByIsActiveTrue(PageRequest.of(0, 10));

        // Then
        assertEquals(2, activePage.getTotalElements());
        activePage.getContent().forEach(prop ->
                assertTrue(prop.getIsActive(), "All returned properties should be active")
        );
    }

    // ========================================================================
    // findByCategoryIgnoreCaseAndIsActiveTrue
    // ========================================================================

    @Test
    @DisplayName("Should return active properties filtered by category (case-insensitive)")
    void findByCategoryIgnoreCaseAndIsActiveTrue_Filtered() {
        // When
        Page<Property> result = propertyRepository
                .findByCategoryIgnoreCaseAndIsActiveTrue("application", PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("APPLICATION", result.getContent().get(0).getCategory());
        assertTrue(result.getContent().get(0).getIsActive());
    }

    // ========================================================================
    // existsByKeyIgnoreCase
    // ========================================================================

    @Test
    @DisplayName("Should return true when property key exists (case-insensitive)")
    void existsByKeyIgnoreCase_True() {
        // When
        boolean exists = propertyRepository.existsByKeyIgnoreCase("app_name");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when property key does not exist")
    void existsByKeyIgnoreCase_False() {
        // When
        boolean exists = propertyRepository.existsByKeyIgnoreCase("DOES_NOT_EXIST");

        // Then
        assertFalse(exists);
    }

    // ========================================================================
    // findDistinctActiveCategories
    // ========================================================================

    @Test
    @DisplayName("Should return distinct list of categories from active properties")
    void findDistinctActiveCategories_ReturnsList() {
        // When
        List<String> categories = propertyRepository.findDistinctActiveCategories();

        // Then
        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertTrue(categories.contains("APPLICATION"));
        assertTrue(categories.contains("SECURITY"));
    }
}
