package com.configvault;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test to verify that the Spring Boot application context
 * loads successfully without errors.
 */
@SpringBootTest
@ActiveProfiles("test")
class ConfigVaultApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the application context starts up correctly.
        // If any bean wiring or configuration issue exists, this test will fail.
    }
}
