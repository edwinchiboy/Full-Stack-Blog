CREATE TABLE IF NOT EXISTS  comments (
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    content TEXT NOT NULL,
    author_id VARCHAR(255),
    post_id VARCHAR(255),
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES posts(id)
);