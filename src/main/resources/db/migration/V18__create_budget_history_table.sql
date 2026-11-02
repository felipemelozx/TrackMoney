CREATE TABLE tb_budget_history (
    history_id SERIAL PRIMARY KEY,

    -- Reference to original budget
    budget_id INTEGER NOT NULL,
    account_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,

    -- Time period
    reference_month SMALLINT NOT NULL CHECK (reference_month BETWEEN 1 AND 12),
    reference_year INTEGER NOT NULL CHECK (reference_year >= 2020),

    -- Budget configuration at snapshot time
    percent SMALLINT NOT NULL CHECK (percent BETWEEN 1 AND 100),

    -- Calculated values
    target_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    spent_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    remaining_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    total_income NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    percentage_used NUMERIC(5, 2) NOT NULL DEFAULT 0.00,

    -- Status: EXCEEDED, WITHIN_LIMIT
    status VARCHAR(20) NOT NULL CHECK (status IN ('EXCEEDED', 'WITHIN_LIMIT')),

    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_budget_history_budget FOREIGN KEY (budget_id)
        REFERENCES tb_budget(budget_id),
    CONSTRAINT fk_budget_history_account FOREIGN KEY (account_id)
        REFERENCES tb_account(account_id),
    CONSTRAINT fk_budget_history_category FOREIGN KEY (category_id)
        REFERENCES tb_category(category_id)
);

-- Create unique constraint to prevent duplicate history entries
CREATE UNIQUE INDEX idx_budget_history_unique_period
    ON tb_budget_history(budget_id, reference_month, reference_year);

-- Create index for efficient queries by user/account and date
CREATE INDEX idx_budget_history_account_date
    ON tb_budget_history(account_id, reference_year DESC, reference_month DESC);
