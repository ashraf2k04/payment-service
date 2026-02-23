# Payment Service – Spring Boot Demo

Simplified **payment lifecycle simulation** backend (no real money involved).  
Built with Spring Boot 3 + Java 21. Focuses on domain logic, strict state transitions, ownership checks, role-based access, and proper JWT revocation.

**Important**: This is an educational / portfolio demo project.

## Features

- Payment states: CREATED → AUTHORIZED → CAPTURED → REFUNDED
- User ownership enforcement (users act only on their own payments)
- Admin override (ROLE_ADMIN can act on any payment + refund)
- Server-side JWT revocation (JTI + sessions table)
- Logout actually invalidates tokens
- @PreAuthorize method security
- Simulated ISO 20022 XML ingestion (placeholder)
- Clear separation 401 (unauthenticated) vs 403 (forbidden/ownership)

## How the Payment Flow Works

1. User registers & logs in → gets JWT (with JTI)
2. Create payment → status CREATED (linked to user)
3. Authorize payment → status AUTHORIZED (owner or admin)
4. Capture payment → status CAPTURED (owner or admin)
5. Refund payment → status REFUNDED (admin only)

**Key protections**
- State machine prevents invalid transitions
- Ownership checked in service layer
- Admin can bypass ownership
- Server-side token revocation (not just short expiry)

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 15+ (or use H2 for quick testing)

### Local Development

1. Clone the repo
   ```
   git clone https://github.com/ashraf2k04/payment-service.git
   cd payment-service
   ```

2. Copy and configure environment
   ```
   cp .env.example .env
   # Edit .env → set JWT_SECRET, DB credentials, etc.
   ```

3. Run with H2 (quick, in-memory)
   ```
   mvn spring-boot:run -Dspring.profiles.active=h2
   ```

4. Run with PostgreSQL (default profile)
   ```
   mvn spring-boot:run
   ```

Application starts on: ```http://localhost:8080```

## REST API Endpoints

All protected endpoints require:

Authorization: Bearer <JWT_TOKEN>

| Method | Path                              | Required Role                        | Description                              |
|--------|-----------------------------------|--------------------------------------|------------------------------------------|
| POST   | /api/auth/register                | Public                               | Register new user                        |
| POST   | /api/auth/login                   | Public                               | Login → get JWT                          |
| POST   | /api/payments                     | ROLE_USER, ROLE_ADMIN                | Create payment                           |
| POST   | /api/payments/{id}/authorize      | ROLE_USER (own only), ROLE_ADMIN     | Authorize payment                        |
| POST   | /api/payments/{id}/capture        | ROLE_USER (own only), ROLE_ADMIN     | Capture (charge) payment                 |
| POST   | /api/payments/{id}/refund         | ROLE_ADMIN only                      | Refund payment                           |
| GET    | /api/payments/{id}                | ROLE_USER (own only), ROLE_ADMIN     | Get payment details                      |

### Example Bodies & Responses

**Register** (201 Created)
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "password": "P@ssw0rd!",
  "role": "ROLE_USER"
}
```

→ 
```
{
  "id": "uuid-here",
  "username": "alice",
  "role": "ROLE_USER"
}
```

**Login** (200 OK)
```
POST /api/auth/login
{ "username": "alice", "password": "P@ssw0rd!" }
```

→ 
```
{ "token": "eyJhbGciOi..." }
```

**Create Payment** (201 Created)
```
POST /api/payments
{ "amount": 1000, "currency": "USD", "reference": "ORDER123" }
```

→ 
```
{
  "id": "uuid-payment",
  "status": "CREATED",
  "amount": 1000,
  ...
}
```

Authorize / Capture / Refund return 200 OK with updated status.

### cURL Full Lifecycle Example

```
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"P@ssw0rd!","role":"ROLE_USER"}'

# 2. Login & save token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"P@ssw0rd!"}' | jq -r .token)

# 3. Create payment
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount":1000,"currency":"USD","reference":"ORDER123"}'

# 4. Authorize (replace {id})
curl -X POST http://localhost:8080/api/payments/{id}/authorize \
  -H "Authorization: Bearer $TOKEN"
```

## Areas for Improvement (Roadmap)

| # | Area                        | Priority | Suggestion                                           |
|---|-----------------------------|----------|------------------------------------------------------|
| 1 | Setup instructions          | *****    | Already improved (env, H2 / PostgreSQL profiles)     |
| 2 | API documentation           | ****     | Add springdoc-openapi → Swagger UI at /swagger-ui.html |
| 3 | Error response examples     | ****     | Show standard JSON error format                      |
| 4 | Database schema visibility  | ***      | Add text ERD or payments table columns               |
| 5 | ISO 20022 XML example       | ***      | Add small XML snippet + curl example                 |
| 6 | License & Contributing      | ****     | Add MIT LICENSE + basic Contributing.md              |

Made in Kolkata, West Bengal with ☕
