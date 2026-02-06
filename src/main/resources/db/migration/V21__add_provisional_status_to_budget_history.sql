-- Drop the old check constraint on status
ALTER TABLE tb_budget_history
DROP CONSTRAINT IF EXISTS tb_budget_history_status_check;

-- Add new check constraint that includes PROVISIONAL status
ALTER TABLE tb_budget_history
ADD CONSTRAINT tb_budget_history_status_check
CHECK (status IN ('EXCEEDED', 'WITHIN_LIMIT', 'PROVISIONAL'));
