package com.configvault.repository;

import com.configvault.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Property} entity persistence operations.
 *
 * <p>Provides standard CRUD operations via {@link JpaRepository} along with
 * custom query methods for case-insensitive key lookups, active property
 * filtering, category-based queries, and distinct category retrieval.</p>
 *
 * @author ConfigVault Team
 * @since 1.0.0
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    /**
     * Finds a property by its key, ignoring case.
     *
     * @param key the property key to search for
     * @return an {@link Optional} containing the property if found, or empty otherwise
     */
    Optional<Property> findByKeyIgnoreCase(String key);

    /**
     * Retrieves a paginated list of all active properties.
     *
     * @param pageable pagination and sorting information
     * @return a {@link Page} of active properties
     */
    Page<Property> findByIsActiveTrue(Pageable pageable);

    /**
     * Retrieves a paginated list of active properties filtered by category (case-insensitive).
     *
     * @param category the category to filter by
     * @param pageable pagination and sorting information
     * @return a {@link Page} of active properties matching the given category
     */
    Page<Property> findByCategoryIgnoreCaseAndIsActiveTrue(String category, Pageable pageable);

    /**
     * Checks whether a property with the given key exists (case-insensitive).
     *
     * @param key the property key to check
     * @return {@code true} if a property with the given key exists, {@code false} otherwise
     */
    boolean existsByKeyIgnoreCase(String key);

    /**
     * Retrieves all distinct categories from active properties, ordered alphabetically.
     *
     * @return a sorted list of distinct active category names
     */
    @Query("SELECT DISTINCT p.category FROM Property p WHERE p.isActive = true ORDER BY p.category")
    List<String> findDistinctActiveCategories();
}
