-- ==============================================================================
-- ConfigVault Database Schema
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS configvault_db;
USE configvault_db;

-- ==============================================================================
-- Properties Table
-- ==============================================================================
CREATE TABLE IF NOT EXISTS properties (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_key       VARCHAR(255)  NOT NULL UNIQUE,
    property_value     VARCHAR(1000) NOT NULL,
    category           VARCHAR(100),
    is_active          BOOLEAN       NOT NULL DEFAULT TRUE,
    created_date       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Indexes for frequent queries
    INDEX idx_property_key (property_key),
    INDEX idx_category     (category),
    INDEX idx_is_active    (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==============================================================================
-- Sample Seed Data
-- ==============================================================================
INSERT INTO properties (property_key, property_value, category, is_active) VALUES
    ('APP_NAME',        'ConfigVault',                       'APPLICATION', TRUE),
    ('APP_VERSION',     '1.0.0',                             'APPLICATION', TRUE),
    ('MAX_UPLOAD_SIZE', '10MB',                              'APPLICATION', TRUE),
    ('DB_POOL_SIZE',    '10',                                'DATABASE',    TRUE),
    ('DB_TIMEOUT',      '30000',                             'DATABASE',    TRUE),
    ('CACHE_TTL',       '3600',                              'CACHE',       TRUE),
    ('LOG_LEVEL',       'INFO',                              'LOGGING',     TRUE),
    ('SMTP_HOST',       'smtp.gmail.com',                    'EMAIL',       TRUE),
    ('SMTP_PORT',       '587',                               'EMAIL',       TRUE),
    ('JWT_SECRET',      'super-secret-jwt-key-12345',        'SECURITY',    TRUE),
    ('AWS_SECRET_KEY',  'AKIAIOSFODNN7EXAMPLE',              'SECURITY',    TRUE),
    ('SWAGGER_PASSWORD','swagger-admin-pass',                'SECURITY',    TRUE)
ON DUPLICATE KEY UPDATE property_value = VALUES(property_value);
