CREATE TABLE  IF NOT EXISTS posts (
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    title VARCHAR(200) NOT NULL,
    excerpt VARCHAR(500),
    content TEXT NOT NULL,
    meta_description VARCHAR(500),
    meta_keywords VARCHAR(200),
    featured_image VARCHAR(500),
    status VARCHAR(50) DEFAULT 'DRAFT',
    author_id VARCHAR(255),
    category_id VARCHAR(255),
    published_at TIMESTAMP,
    slug VARCHAR(500),
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES categories(id)
);