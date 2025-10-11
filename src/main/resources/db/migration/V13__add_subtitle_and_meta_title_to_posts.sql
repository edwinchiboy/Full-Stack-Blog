-- Add subtitle and meta_title columns to posts table
ALTER TABLE posts ADD COLUMN IF NOT EXISTS subtitle VARCHAR(300);
ALTER TABLE posts ADD COLUMN IF NOT EXISTS meta_title VARCHAR(200);