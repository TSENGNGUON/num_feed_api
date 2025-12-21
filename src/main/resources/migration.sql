-- Migration script to convert all ID columns from INTEGER to UUID
-- 
-- IMPORTANT: This script DROPS all tables and lets Hibernate recreate them with UUID types
-- Only use this if you're in development and don't have important data!
-- 
-- For production, you'll need a more complex migration strategy with data preservation

-- Step 1: Drop all foreign key constraints and tables in correct order
DROP TABLE IF EXISTS reposts CASCADE;
DROP TABLE IF EXISTS post_images CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS forgot_password CASCADE;
DROP TABLE IF EXISTS user_following CASCADE;
DROP TABLE IF EXISTS _users CASCADE;

-- Step 2: Hibernate will automatically recreate all tables with UUID columns
-- when the application starts with ddl-auto: update

-- After running this script, restart your Spring Boot application
-- Hibernate will recreate all tables with the correct UUID column types
