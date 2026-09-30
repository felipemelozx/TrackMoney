# Especificação — Deletar Conta (Hard Delete)

Endpoint para exclusão **permanente** da conta do usuário autenticado. Apaga o usuário e **todos os dados relacionados**: conta, transações, budgets, budget history, pots, lançamentos recorrentes e registros legados de relatórios/transferências.

---

## Endpoint

```
DELETE {BASE_URL}/api/v1/user
```

- `BASE_URL` (dev local): `http://localhost:8080`
- `BASE_URL` (produção): `https://api.trackmoney.fun`

## Autenticação

- Header `Authorization: Bearer <access_token>`
- Necessária autoridade `USER_ROLES` (usuário verificado/logado)

## Requisição

### Body (JSON)

| Campo      | Tipo   | Obrigatório | Validação |
|------------|--------|-------------|-----------|
| `password` | string | sim         | `@NotBlank` — não pode ser vazio |

```json
{
  "password": "SuaSenhaAtual123#"
}
```

## Respostas

### 204 No Content — Sucesso

Sem corpo. A conta e todos os dados foram apagados.

### 400 Bad Request — Senha incorreta ou usuário inexistente

```json
{
  "success": false,
  "message": "Account deletion failed.",
  "data": null,
  "errors": [
    { "field": "Password", "message": "Incorrect password." }
  ],
  "timestamp": "2026-08-15T16:40:00"
}
```

### 400 Bad Request — Body inválido (validação)

Quando `password` está ausente/vazio, o `RestExceptionHandler` responde com o padrão de validação da API:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": [
    { "field": "password", "message": "Password is required" }
  ],
  "timestamp": "2026-08-15T16:40:00"
}
```

> Erros de validação vêm com `field` em minúsculo (`password`); erro de senha incorreta vem com `field` capitalizado (`Password`). No front, trate pelo `message` ou considere ambos.

---

## Exemplos de uso

### cURL

```bash
curl -X DELETE https://api.trackmoney.fun/api/v1/user \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{ "password": "SuaSenhaAtual123#" }'
```

### JavaScript (fetch)

```js
const response = await fetch(`${API_BASE}/user`, {
  method: 'DELETE',
  headers: {
    Authorization: `Bearer ${accessToken}`,
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({ password }),
});

if (response.status === 204) {
  // Conta deletada com sucesso
  // => limpar tokens e dados locais, redirecionar para /login
} else {
  const body = await response.json();
  const message = body.errors?.[0]?.message ?? body.message;
  // Exibir erro ao usuário (ex.: "Incorrect password.")
}
```

---

## Comportamento interno (para contexto)

1. Busca o usuário pelo `userId` do token autenticado — inexistente => `400`.
2. Compara a senha com BCrypt (`PasswordEncoder.matches`) — incorreta => `400`.
3. Deleta em cascata, em ordem: transferências e relatórios legados → `budget_history` → `budgets` → `pots` → `recurring` → `transactions` → conta → usuário.
4. Tudo roda em uma transação (`@Transactional`): se algo falhar, nada é apagado.
5. Após a exclusão, o usuário não existe mais no banco — **o JWT atual fica inválido automaticamente** (o filtro não encontra o usuário).

---

## Recomendações para o frontend

- Mostrar um **modal de confirmação** pedindo a senha antes de chamar o endpoint (ação destrutiva).
- Desabilitar o botão/inputs enquanto a requisição está em andamento.
- Em `204`: limpar tokens (`access`/`refresh`), dados em cache (React Query/Context/Redux) e redirecionar para a tela de login.
- Em `400`: manter o usuário logado e exibir o erro do campo `errors[0].message` (não deslogar).
- Tratar erro de rede/timeout como falha genérica, mantendo o usuário logado.
