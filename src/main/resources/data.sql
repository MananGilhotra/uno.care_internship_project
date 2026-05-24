-- =============================================================================
-- ConfigVault - Sample Data Initialization
-- =============================================================================
-- This file inserts sample configuration properties into the database
-- on application startup. It is executed when spring.sql.init.mode=always
-- is set in the active profile configuration.
--
-- NOTE: INSERT IGNORE ensures idempotent execution - duplicate keys are
-- silently skipped, preventing errors on subsequent application restarts.
-- =============================================================================

INSERT INTO properties (property_key, property_value, category, is_active, created_date, last_modified_date) VALUES
('APP_NAME', 'ConfigVault', 'APPLICATION', true, NOW(), NOW()),
('APP_VERSION', '1.0.0', 'APPLICATION', true, NOW(), NOW()),
('MAX_UPLOAD_SIZE', '10MB', 'APPLICATION', true, NOW(), NOW()),
('DB_POOL_SIZE', '10', 'DATABASE', true, NOW(), NOW()),
('DB_TIMEOUT', '30000', 'DATABASE', true, NOW(), NOW()),
('CACHE_TTL', '3600', 'CACHE', true, NOW(), NOW()),
('LOG_LEVEL', 'INFO', 'LOGGING', true, NOW(), NOW()),
('SMTP_HOST', 'smtp.gmail.com', 'EMAIL', true, NOW(), NOW()),
('SMTP_PORT', '587', 'EMAIL', true, NOW(), NOW()),
('JWT_SECRET', 'super-secret-jwt-key-12345', 'SECURITY', true, NOW(), NOW()),
('AWS_SECRET_KEY', 'AKIAIOSFODNN7EXAMPLE', 'SECURITY', true, NOW(), NOW()),
('SWAGGER_PASSWORD', 'swagger-admin-pass', 'SECURITY', true, NOW(), NOW());


