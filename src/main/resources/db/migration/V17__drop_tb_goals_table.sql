-- Drop tb_goals table as it's redundant with tb_pots
-- Both served similar purposes (tracking financial goals/savings)
-- tb_pots provides more complete functionality with account balance integration

DROP TABLE IF EXISTS tb_goals CASCADE;