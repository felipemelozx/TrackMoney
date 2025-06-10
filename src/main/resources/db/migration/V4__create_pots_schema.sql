CREATE TABLE tb_pots (
    pot_id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    target_amount BIGINT,
    current_amount BIGINT
);