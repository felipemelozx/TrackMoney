INSERT INTO public.tb_category (name, color)
SELECT v.name, v.color
FROM (
  VALUES
    ('Alimentação', '#FF6B6B'),
    ('Transporte', '#4ECDC4'),
    ('Moradia', '#1A535C'),
    ('Educação', '#FF9F1C'),
    ('Saúde', '#2EC4B6'),
    ('Lazer', '#9B5DE5'),
    ('Vestuário', '#F15BB5'),
    ('Serviços', '#00BBF9'),
    ('Assinaturas', '#E36414'),
    ('Supermercado', '#FFBE0B'),
    ('Contas (água, luz, internet)', '#3A86FF'),
    ('Impostos e taxas', '#8338EC'),
    ('Doações', '#06D6A0'),
    ('Investimentos', '#118AB2'),
    ('Salário', '#06D6A0'),
    ('Rendimentos', '#FFD166'),
    ('Outros', '#8D99AE')
) AS v(name, color)
