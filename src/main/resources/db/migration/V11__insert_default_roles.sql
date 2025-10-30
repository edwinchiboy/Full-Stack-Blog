-- Insert default roles
INSERT INTO roles (id, created_at, updated_at, name)
VALUES
    ('role-reader-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROLE_READER'),
    ('role-admin-001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ROLE_ADMIN')
ON CONFLICT (id) DO NOTHING;