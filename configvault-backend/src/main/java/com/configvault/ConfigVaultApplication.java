package com.configvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the ConfigVault application.
 *
 * <p>ConfigVault is a Property Management System that allows administrators
 * to manage application configuration properties securely. It provides
 * RESTful APIs for CRUD operations on configuration properties with
 * built-in support for value masking, rate limiting, and caching.</p>
 *
 * @author ConfigVault Team
 * @version 1.0.0
 * @since 2026-05-24
 */
@SpringBootApplication
@EnableCaching
public class ConfigVaultApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigVaultApplication.class, args);
    }
}
