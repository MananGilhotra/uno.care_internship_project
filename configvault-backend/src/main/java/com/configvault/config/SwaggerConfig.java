package com.configvault.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * Provides the beans necessary to generate and customize the API documentation.
 */
@Configuration
@Slf4j
public class SwaggerConfig {

    /**
     * Customizes the OpenAPI definition with application-specific details
     * such as title, description, version, and contact information.
     *
     * @return the configured OpenAPI specification
     */
    @Bean
    public OpenAPI configVaultOpenAPI() {
        log.info("Initializing OpenAPI/Swagger documentation configuration");

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("ConfigVault API")
                        .description("Enterprise-grade Property Management System API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ConfigVault Team"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));

        log.info("OpenAPI/Swagger documentation configuration initialized successfully");
        return openAPI;
    }
}
