ALTER TABLE tb_budget
    DROP COLUMN user_id,
    DROP COLUMN current_amount,
    DROP COLUMN reset_day;

ALTER TABLE tb_budget
    RENAME COLUMN target_amount TO percent;

ALTER TABLE tb_budget
    ADD CONSTRAINT chk_percent_range CHECK (percent BETWEEN 1 AND 100);
