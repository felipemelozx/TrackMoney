ALTER TABLE tb_user
ADD COLUMN account_id INTEGER UNIQUE,
ADD CONSTRAINT fk_user_account FOREIGN KEY (account_id) REFERENCES tb_account(account_id);