package com.configvault.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Spring Cache abstraction.
 *
 * <p>Enables caching and configures a {@link ConcurrentMapCacheManager}
 * with predefined cache regions for properties and categories. This
 * provides lightweight, in-memory caching suitable for single-instance
 * deployments.</p>
 *
 * <p>For distributed deployments, consider replacing this with a
 * Redis or Hazelcast-backed cache manager.</p>
 *
 * @author ConfigVault Team
 * @since 1.0.0
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * Creates a {@link CacheManager} bean backed by {@link ConcurrentMapCacheManager}.
     *
     * <p>Registers the following cache regions:</p>
     * <ul>
     *   <li><strong>properties</strong> - caches individual property lookups and listings</li>
     *   <li><strong>categories</strong> - caches the distinct active category list</li>
     * </ul>
     *
     * @return the configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing ConcurrentMapCacheManager with caches: [properties, categories]");
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("properties", "categories");
        log.info("Cache manager initialized successfully");
        return cacheManager;
    }
}
