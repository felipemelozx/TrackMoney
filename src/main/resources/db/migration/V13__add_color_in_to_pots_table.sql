ALTER TABLE tb_pots
    ADD COLUMN color VARCHAR(20);

ALTER TABLE tb_pots
    ALTER COLUMN color SET DEFAULT 'DARK_BLUE';

ALTER TABLE tb_pots
    ADD CONSTRAINT chk_color_valid CHECK (color IN (
        'DARK_BLUE',
        'GRAPHITE_BLUE',
        'GRAYISH_BLUE',
        'SOFT_BLACK',
        'SLATE_BLUE'
    ));

ALTER TABLE tb_pots
    ALTER COLUMN color SET NOT NULL;