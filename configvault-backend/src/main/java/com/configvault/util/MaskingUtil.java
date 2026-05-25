package com.configvault.util;

import com.configvault.constants.AppConstants;

/**
 * Utility class for masking sensitive property values.
 *
 * <p>This class provides methods to determine whether a given property key
 * is considered restricted (i.e., sensitive) and to mask its value accordingly.
 * Restricted keys are defined in {@link AppConstants#RESTRICTED_KEYS}.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     String displayValue = MaskingUtil.maskValue("API_SECRET", "my-secret");
 *     // Returns "********"
 *
 *     String displayValue = MaskingUtil.maskValue("APP_NAME", "ConfigVault");
 *     // Returns "ConfigVault"
 * </pre>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
public final class MaskingUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class cannot be instantiated
     */
    private MaskingUtil() {
        throw new UnsupportedOperationException("Cannot instantiate utility class MaskingUtil");
    }

    /**
     * Checks whether the given key is classified as restricted.
     *
     * <p>The key is normalized to uppercase and trimmed before comparison
     * against the set of restricted keys defined in {@link AppConstants}.</p>
     *
     * @param key the property key to check; may be null
     * @return {@code true} if the key is restricted, {@code false} otherwise
     */
    public static boolean isRestrictedKey(String key) {
        if (key == null) {
            return false;
        }
        return AppConstants.RESTRICTED_KEYS.contains(key.toUpperCase().trim());
    }

    /**
     * Returns the property value or a masked placeholder if the key is restricted.
     *
     * <p>If the key is identified as restricted by {@link #isRestrictedKey(String)},
     * this method returns {@link AppConstants#MASK_VALUE} instead of the actual value.</p>
     *
     * @param key   the property key used to determine if masking is needed
     * @param value the actual property value
     * @return the original value if the key is not restricted, or the mask value otherwise
     */
    public static String maskValue(String key, String value) {
        if (isRestrictedKey(key)) {
            return AppConstants.MASK_VALUE;
        }
        return value;
    }
}
