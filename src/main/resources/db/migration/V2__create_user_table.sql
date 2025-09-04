CREATE TABLE IF NOT EXISTS  users (
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role_id VARCHAR(255),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);