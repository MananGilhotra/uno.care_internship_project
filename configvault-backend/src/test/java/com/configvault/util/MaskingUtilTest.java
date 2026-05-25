package com.configvault.util;

import com.configvault.constants.AppConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MaskingUtil}.
 * Verifies restricted-key detection and value-masking behavior.
 */
class MaskingUtilTest {

    // ========================================================================
    // isRestrictedKey — Positive Cases
    // ========================================================================

    @ParameterizedTest
    @ValueSource(strings = {
            "JWT_SECRET",
            "SWAGGER_PASSWORD",
            "AWS_SECRET_KEY"
    })
    @DisplayName("Should identify known restricted keys as restricted")
    void isRestrictedKey_True(String key) {
        assertTrue(MaskingUtil.isRestrictedKey(key),
                key + " should be identified as a restricted key");
    }

    // ========================================================================
    // isRestrictedKey — Negative Cases
    // ========================================================================

    @ParameterizedTest
    @ValueSource(strings = {
            "APP_NAME",
            "LOG_LEVEL",
            "SERVER_PORT",
            "FEATURE_FLAG",
            "DB_HOST"
    })
    @DisplayName("Should identify non-restricted keys as unrestricted")
    void isRestrictedKey_False(String key) {
        assertFalse(MaskingUtil.isRestrictedKey(key),
                key + " should NOT be identified as a restricted key");
    }

    // ========================================================================
    // isRestrictedKey — Case Insensitivity
    // ========================================================================

    @Test
    @DisplayName("Should detect restricted keys regardless of case")
    void isRestrictedKey_CaseInsensitive() {
        assertTrue(MaskingUtil.isRestrictedKey("jwt_secret"));
        assertTrue(MaskingUtil.isRestrictedKey("Jwt_Secret"));
        assertTrue(MaskingUtil.isRestrictedKey("JWT_SECRET"));
        assertTrue(MaskingUtil.isRestrictedKey("swagger_password"));
        assertTrue(MaskingUtil.isRestrictedKey("aws_secret_key"));
    }

    // ========================================================================
    // maskValue — Restricted Key
    // ========================================================================

    @Test
    @DisplayName("Should return mask value (********) for a restricted key")
    void maskValue_ReturnsStars_ForRestrictedKey() {
        // Given
        String key = "JWT_SECRET";
        String originalValue = "super-secret-token-12345";

        // When
        String maskedValue = MaskingUtil.maskValue(key, originalValue);

        // Then
        assertEquals(AppConstants.MASK_VALUE, maskedValue);
        assertNotEquals(originalValue, maskedValue);
    }

    // ========================================================================
    // maskValue — Non-Restricted Key
    // ========================================================================

    @Test
    @DisplayName("Should return original value for a non-restricted key")
    void maskValue_ReturnsOriginal_ForNonRestrictedKey() {
        // Given
        String key = "APP_NAME";
        String originalValue = "ConfigVault";

        // When
        String maskedValue = MaskingUtil.maskValue(key, originalValue);

        // Then
        assertEquals(originalValue, maskedValue);
    }

    // ========================================================================
    // Edge Cases
    // ========================================================================

    @Test
    @DisplayName("Should handle null key without throwing exception")
    void isRestrictedKey_NullKey() {
        assertFalse(MaskingUtil.isRestrictedKey(null));
    }

    @Test
    @DisplayName("Should return original value when key is null")
    void maskValue_NullKey_ReturnsOriginal() {
        String value = "some-value";
        assertEquals(value, MaskingUtil.maskValue(null, value));
    }
}
