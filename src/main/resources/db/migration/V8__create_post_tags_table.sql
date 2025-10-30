create TABLE IF NOT EXISTS post_tags (
 id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    post_id VARCHAR(255) NOT NULL,
    tag_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id),
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id)
);