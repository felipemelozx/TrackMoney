-- Drop percentage_used column from tb_budget_history
-- This column is no longer needed as we're replacing it with transaction details
ALTER TABLE tb_budget_history DROP COLUMN IF EXISTS percentage_used;
