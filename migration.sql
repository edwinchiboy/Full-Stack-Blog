-- Migration script to convert category_id to category enum
-- Run this after the application has created the category column

-- Step 1: Add the new category column (if not already created by JPA)
-- ALTER TABLE posts ADD COLUMN category VARCHAR(255);

-- Step 2: Migrate data from category_id to category enum
-- You'll need to update this based on your actual category UUIDs
UPDATE posts
SET category = CASE
    WHEN category_id = (SELECT id FROM categories WHERE name = 'DeFi') THEN 'DEFI'
    WHEN category_id = (SELECT id FROM categories WHERE name = 'NFTs') THEN 'NFTS'
    WHEN category_id = (SELECT id FROM categories WHERE name = 'Blockchain') THEN 'BLOCKCHAIN'
    WHEN category_id = (SELECT id FROM categories WHERE name = 'Trading') THEN 'TRADING'
    WHEN category_id = (SELECT id FROM categories WHERE name = 'Security') THEN 'SECURITY'
    WHEN category_id = (SELECT id FROM categories WHERE name = 'Web3') THEN 'WEB3'
    ELSE NULL
END
WHERE category_id IS NOT NULL;

-- Step 3: Drop the old category_id column
ALTER TABLE posts DROP COLUMN IF EXISTS category_id;

-- Step 4: Drop the categories table (no longer needed)
DROP TABLE IF EXISTS categories CASCADE;

-- Verify the migration
SELECT id, title, category FROM posts LIMIT 10;