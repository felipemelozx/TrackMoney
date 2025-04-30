CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE tb_user (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tb_account (
    account_id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    balance NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    is_account_default BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tb_user(user_id)
);

CREATE TABLE tb_category (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE tb_transaction (
    transaction_id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    description TEXT,
    transaction_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES tb_account(account_id),
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES tb_category(category_id)
);

CREATE TABLE tb_transfer (
    transfer_id SERIAL PRIMARY KEY,
    amount NUMERIC(19, 4) NOT NULL,
    transfer_date TIMESTAMP NOT NULL,
    from_account_id INTEGER NOT NULL,
    to_account_id INTEGER NOT NULL,
    CONSTRAINT fk_from_account FOREIGN KEY (from_account_id) REFERENCES tb_account(account_id),
    CONSTRAINT fk_to_account FOREIGN KEY (to_account_id) REFERENCES tb_account(account_id)
);

CREATE TABLE tb_report (
    report_id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    total_income NUMERIC(19, 4) NOT NULL,
    total_expense NUMERIC(19, 4) NOT NULL,
    CONSTRAINT fk_report_account FOREIGN KEY (account_id) REFERENCES tb_account(account_id)
);

CREATE TABLE tb_goals (
    goals_id SERIAL PRIMARY KEY,
    goal VARCHAR(255) NOT NULL,
    account_id INTEGER NOT NULL,
    target_amount NUMERIC(19, 4) NOT NULL,
    current_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    progress INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_goal_account FOREIGN KEY (account_id) REFERENCES tb_account(account_id)
);
