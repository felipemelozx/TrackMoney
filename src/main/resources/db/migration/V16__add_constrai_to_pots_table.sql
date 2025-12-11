
UPDATE public.tb_pots
SET current_amount = 0
WHERE current_amount IS NULL;


ALTER TABLE public.tb_pots
ALTER COLUMN current_amount SET NOT NULL;

ALTER TABLE public.tb_pots
ALTER COLUMN current_amount SET DEFAULT 0;
