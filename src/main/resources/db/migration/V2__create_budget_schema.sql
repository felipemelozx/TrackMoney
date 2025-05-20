CREATE TABLE tb_budget(
    budget_id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    account_id SERIAL NOT NULL,
    category_id SERIAL NOT NULL,
    target_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    reset_day INTEGER,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tb_user(user_id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES tb_account(account_id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES tb_category(category_id)
);