-- Add indexes for metrics and analytics queries
-- These indexes optimize the performance of aggregation queries used in the Metrics module

-- Index for monthly summary queries (account + date + type)
-- Used by: GET /api/v1/metrics/monthly-summary
CREATE INDEX idx_tx_account_date_type
  ON tb_transaction(account_id, transaction_date DESC, transaction_type);

-- Index for category breakdown queries
-- Used by: GET /api/v1/metrics/by-category
CREATE INDEX idx_tx_account_category_date
  ON tb_transaction(account_id, category_id, transaction_date);

-- Partial index for expense-only queries (performance optimization)
-- Used by: GET /api/v1/metrics/by-category and GET /api/v1/metrics/overview
CREATE INDEX idx_tx_expenses_account_date
  ON tb_transaction(account_id, transaction_date)
  WHERE transaction_type = 'EXPENSE';

-- Index for budget history lookups (account + year + month)
-- Used by: GET /api/v1/metrics/budgets/performance and GET /api/v1/metrics/overview
CREATE INDEX idx_budget_history_account_year_month
  ON tb_budget_history(account_id, reference_year DESC, reference_month DESC);

-- Comment explaining the purpose of these indexes
COMMENT ON INDEX idx_tx_account_date_type IS 'Optimizes monthly summary aggregation queries';
COMMENT ON INDEX idx_tx_account_category_date IS 'Optimizes category breakdown aggregation queries';
COMMENT ON INDEX idx_tx_expenses_account_date IS 'Optimizes expense-only queries with partial index';
COMMENT ON INDEX idx_budget_history_account_year_month IS 'Optimizes budget history lookups by year and month';
