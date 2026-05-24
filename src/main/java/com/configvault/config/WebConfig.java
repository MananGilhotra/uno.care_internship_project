package com.configvault.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

/**
 * Web MVC configuration for the ConfigVault application.
 *
 * <p>Configures Cross-Origin Resource Sharing (CORS) to allow
 * API access from any origin. This is suitable for development
 * and scenarios where the API is consumed by various front-end
 * applications.</p>
 *
 * <p>For production deployments, consider restricting the allowed
 * origins to specific domains.</p>
 *
 * @author ConfigVault Team
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures CORS mappings for the application.
     *
     * <p>Allows all origins, specified HTTP methods (GET, POST, PUT, DELETE, OPTIONS),
     * and all headers for all URL patterns.</p>
     *
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        log.info("Configuring CORS mappings - allowing all origins with methods: GET, POST, PUT, DELETE, OPTIONS");

        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");

        log.info("CORS mappings configured successfully");
    }
}
