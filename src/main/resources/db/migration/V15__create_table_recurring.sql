CREATE TABLE tb_recurring (
    recurring_id SERIAL PRIMARY KEY,

    frequency VARCHAR(50) NOT NULL CHECK (
        frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')
    ),

    next_date TIMESTAMP NOT NULL,
    last_date TIMESTAMP NULL,

    account_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    description VARCHAR(255) NULL,
    transaction_name VARCHAR(50) NULL,

    CONSTRAINT fk_recurring_account FOREIGN KEY (account_id)
        REFERENCES tb_account (account_id),

    CONSTRAINT fk_recurring_category FOREIGN KEY (category_id)
        REFERENCES tb_category (category_id)
);
