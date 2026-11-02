-- Add is_delete column to tb_user table
ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS is_delete BOOLEAN NOT NULL DEFAULT FALSE;
