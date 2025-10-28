-- Add parent_comment_id column to comments table for reply functionality
ALTER TABLE comments
ADD COLUMN parent_comment_id VARCHAR(255);

-- Add foreign key constraint to reference parent comment
ALTER TABLE comments
ADD CONSTRAINT fk_parent_comment
FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE;

-- Add index for better query performance
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);