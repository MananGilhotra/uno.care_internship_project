package com.configvault.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA entity representing a configuration property stored in the system.
 *
 * <p>Each property consists of a unique key-value pair, optionally categorized
 * for organizational purposes. Properties can be marked as active or inactive,
 * and all keys are automatically normalized to uppercase for consistency.</p>
 *
 * <p>Audit timestamps are managed automatically via Hibernate's
 * {@link CreationTimestamp} and {@link UpdateTimestamp} annotations.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    /**
     * Auto-generated unique identifier for the property.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique property key, stored in uppercase.
     * Used as the primary lookup identifier for configuration properties.
     */
    @Column(name = "property_key", nullable = false, unique = true, length = 255)
    private String key;

    /**
     * The value associated with this property key.
     * May contain sensitive data that is masked in API responses.
     */
    @Column(name = "property_value", nullable = false, length = 1000)
    private String value;

    /**
     * Optional category for organizing related properties (e.g., DATABASE, SECURITY).
     */
    @Column(name = "category", length = 100)
    private String category;

    /**
     * Flag indicating whether this property is currently active.
     * Defaults to {@code true} when a new property is created.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Timestamp recording when this property was first created.
     * This field is set automatically and cannot be updated.
     */
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    /**
     * Timestamp recording when this property was last modified.
     * This field is updated automatically on each entity update.
     */
    @Column(name = "last_modified_date")
    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    /**
     * JPA lifecycle callback invoked before persisting a new property.
     * Normalizes the property key to uppercase for consistent lookups.
     */
    @PrePersist
    public void prePersist() {
        if (key != null) {
            key = key.toUpperCase().trim();
        }
    }

    /**
     * JPA lifecycle callback invoked before updating an existing property.
     * Normalizes the property key to uppercase for consistent lookups.
     */
    @PreUpdate
    public void preUpdate() {
        if (key != null) {
            key = key.toUpperCase().trim();
        }
    }
}
