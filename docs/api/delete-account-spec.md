# Specification — Delete Account (Hard Delete)

Endpoint for **permanent** deletion of the authenticated user's account. Removes the user and **all associated data**: accounts, transactions, budgets, budget history, savings pots, recurring records, and legacy report/transfer entities.

---

## Endpoint

```
DELETE {BASE_URL}/api/v1/user
```

- `BASE_URL` (local dev): `http://localhost:8080`
- `BASE_URL` (production): `https://trackmoney.shardweb.app`

## Authentication

- Header `Authorization: Bearer <access_token>`
- Requires authority `USER_ROLES` (verified and logged-in user)

## Request

### Body (JSON)

| Field | Type | Required | Validation |
|---|---|---|---|
| `password` | string | Yes | `@NotBlank` — cannot be blank |

```json
{
  "password": "YourCurrentPassword123#"
}
```

## Responses

### 204 No Content — Success

No response body. The account and all associated domain data have been permanently deleted.

### 400 Bad Request — Incorrect Password or Nonexistent User

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

### 400 Bad Request — Invalid Body (Validation Error)

When `password` is missing or empty, `RestExceptionHandler` returns the standard API validation response:

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

> Validation errors return with lowercase `field` (`password`), whereas incorrect password errors return with capitalized `field` (`Password`). In the frontend, handle errors via `message` or account for both cases.

---

## Usage Examples

### cURL

```bash
curl -X DELETE https://trackmoney.shardweb.app/api/v1/user \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{ "password": "YourCurrentPassword123#" }'
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
  // Account deleted successfully
  // => clear tokens and local storage, redirect to /login
} else {
  const body = await response.json();
  const message = body.errors?.[0]?.message ?? body.message;
  // Display error message to user (e.g. "Incorrect password.")
}
```

---

## Internal Behavior (Context)

1. Retrieves user by `userId` extracted from the authenticated token — nonexistent => `400`.
2. Verifies password hash using BCrypt (`PasswordEncoder.matches`) — incorrect => `400`.
3. Cascades deletion in order: legacy reports/transfers -> `budget_history` -> `budgets` -> `pots` -> `recurring` -> `transactions` -> account -> user.
4. Executed entirely within a single transaction (`@Transactional`): any failure triggers a full rollback.
5. Once deleted, the user ceases to exist in the database — **the existing JWT becomes invalid automatically** (authentication filter will not find the user).

---

## Frontend Recommendations

- Display a **confirmation modal** requiring password re-entry prior to triggering the endpoint (destructive action).
- Disable buttons/inputs while the deletion request is pending.
- On `204`: Clear tokens (`access`/`refresh`), cached state, and redirect to login screen.
- On `400`: Keep the user logged in and display the validation/error message from `errors[0].message`.
- Handle network errors/timeouts gracefully as a generic failure without logging the user out.
