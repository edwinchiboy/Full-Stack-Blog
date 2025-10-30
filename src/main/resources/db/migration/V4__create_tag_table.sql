CREATE TABLE  IF NOT EXISTS  tags(
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(500),
    slug VARCHAR(255)
);