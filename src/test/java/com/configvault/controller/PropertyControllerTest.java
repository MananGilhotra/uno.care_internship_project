package com.configvault.controller;

import com.configvault.dto.request.PropertyRequest;
import com.configvault.dto.response.PagedResponse;
import com.configvault.dto.response.PropertyResponse;
import com.configvault.exception.ResourceNotFoundException;
import com.configvault.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer tests for {@link PropertyController}.
 * Uses {@link WebMvcTest} to slice the Spring context and
 * {@link MockMvc} to perform HTTP requests without starting a full server.
 */
@WebMvcTest(PropertyController.class)
@SuppressWarnings("null")
class PropertyControllerTest {

    @MockBean
    private PropertyService propertyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/properties";

    private PropertyResponse sampleResponse;
    private PropertyRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new PropertyResponse();
        sampleResponse.setId(1L);
        sampleResponse.setKey("APP_NAME");
        sampleResponse.setValue("ConfigVault");
        sampleResponse.setCategory("APPLICATION");
        sampleResponse.setIsActive(true);
        sampleResponse.setIsRestricted(false);
        sampleResponse.setCreatedDate(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        sampleResponse.setLastModifiedDate(LocalDateTime.of(2026, 1, 1, 0, 0, 0));

        sampleRequest = new PropertyRequest();
        sampleRequest.setKey("APP_NAME");
        sampleRequest.setValue("ConfigVault");
        sampleRequest.setCategory("APPLICATION");
    }

    // ========================================================================
    // POST /api/v1/properties  —  Create Property
    // ========================================================================

    @Test
    @DisplayName("POST / — Should create property and return 201 CREATED")
    void createProperty_ReturnsCreated() throws Exception {
        // Given
        when(propertyService.createOrUpdateProperty(any(PropertyRequest.class)))
                .thenReturn(sampleResponse);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.key").value("APP_NAME"))
                .andExpect(jsonPath("$.data.value").value("ConfigVault"));
    }

    @Test
    @DisplayName("POST / — Should return 400 BAD REQUEST when key is empty")
    void createProperty_ValidationError_ReturnsBadRequest() throws Exception {
        // Given — request with blank key violates @NotBlank
        PropertyRequest invalidRequest = new PropertyRequest();
        invalidRequest.setKey("");
        invalidRequest.setValue("SomeValue");
        invalidRequest.setCategory("APPLICATION");

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ========================================================================
    // GET /api/v1/properties/{key}  —  Get Property By Key
    // ========================================================================

    @Test
    @DisplayName("GET /{key} — Should return property when key exists")
    void getPropertyByKey_ReturnsOk() throws Exception {
        // Given
        when(propertyService.getPropertyByKey("APP_NAME")).thenReturn(sampleResponse);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/APP_NAME"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.key").value("APP_NAME"))
                .andExpect(jsonPath("$.data.value").value("ConfigVault"))
                .andExpect(jsonPath("$.data.category").value("APPLICATION"));
    }

    @Test
    @DisplayName("GET /{key} — Should return 404 when property key not found")
    void getPropertyByKey_NotFound_Returns404() throws Exception {
        // Given
        when(propertyService.getPropertyByKey("NON_EXISTENT"))
                .thenThrow(new ResourceNotFoundException("Property", "key", "NON_EXISTENT"));

        // When & Then
        mockMvc.perform(get(BASE_URL + "/NON_EXISTENT"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // ========================================================================
    // GET /api/v1/properties  —  Get Active Properties (Paginated)
    // ========================================================================

    @Test
    @DisplayName("GET / — Should return paginated active properties")
    void getActiveProperties_ReturnsOk() throws Exception {
        // Given
        List<PropertyResponse> content = Collections.singletonList(sampleResponse);
        PagedResponse<PropertyResponse> pagedResponse = new PagedResponse<>(
                content, 0, 10, 1L, 1, true
        );
        when(propertyService.getActiveProperties(0, 10)).thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].key").value("APP_NAME"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    // ========================================================================
    // GET /api/v1/properties/category/{category}
    // ========================================================================

    @Test
    @DisplayName("GET /category/{category} — Should return properties filtered by category")
    void getPropertiesByCategory_ReturnsOk() throws Exception {
        // Given
        List<PropertyResponse> content = Collections.singletonList(sampleResponse);
        PagedResponse<PropertyResponse> pagedResponse = new PagedResponse<>(
                content, 0, 10, 1L, 1, true
        );
        when(propertyService.getPropertiesByCategory(eq("APPLICATION"), anyInt(), anyInt()))
                .thenReturn(pagedResponse);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/category/APPLICATION")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].category").value("APPLICATION"));
    }

    // ========================================================================
    // DELETE /api/v1/properties/{key}  —  Soft Delete
    // ========================================================================

    @Test
    @DisplayName("DELETE /{key} — Should soft-delete property and return 200 OK")
    void softDeleteProperty_ReturnsOk() throws Exception {
        // Given
        PropertyResponse deletedResponse = new PropertyResponse();
        deletedResponse.setId(7L);
        deletedResponse.setKey("LOG_LEVEL");
        deletedResponse.setValue("INFO");
        deletedResponse.setCategory("LOGGING");
        deletedResponse.setIsActive(false);
        deletedResponse.setIsRestricted(false);
        deletedResponse.setCreatedDate(LocalDateTime.of(2026, 1, 1, 0, 0, 0));
        deletedResponse.setLastModifiedDate(LocalDateTime.of(2026, 1, 1, 0, 0, 0));

        when(propertyService.softDeleteProperty("LOG_LEVEL")).thenReturn(deletedResponse);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/LOG_LEVEL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ========================================================================
    // GET /api/v1/properties/categories
    // ========================================================================

    @Test
    @DisplayName("GET /categories — Should return list of distinct active categories")
    void getActiveCategories_ReturnsOk() throws Exception {
        // Given
        List<String> categories = Arrays.asList("APPLICATION", "SECURITY", "DATABASE");
        when(propertyService.getActiveCategories()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("APPLICATION"))
                .andExpect(jsonPath("$.data[1]").value("SECURITY"))
                .andExpect(jsonPath("$.data[2]").value("DATABASE"));
    }
}
