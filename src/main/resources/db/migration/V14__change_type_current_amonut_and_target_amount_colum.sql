ALTER TABLE tb_pots
    ALTER COLUMN target_amount TYPE DECIMAL(19,4),
    ALTER COLUMN current_amount TYPE DECIMAL(19,4);

ALTER TABLE tb_pots
    ALTER COLUMN current_amount SET DEFAULT 0.0000;