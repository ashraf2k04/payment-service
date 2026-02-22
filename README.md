# 💳 Payment Service Backend

A secure, role-based payment processing backend built using **Spring Boot**.  
This project implements JWT authentication, session management, RBAC, ISO 20022 XML parsing, and a controlled payment state machine.

It simulates a real-world payment processing engine with layered security and domain validation.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-3-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Development-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-Testing-09476B?style=for-the-badge&logo=h2&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-6-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-1.18-red?style=for-the-badge)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-jjwt-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

---

# 🚀 Features

## 🔐 Authentication & Security

- JWT-based authentication
- UUID used as token subject
- Token expiration handling
- Custom JWT authentication filter
- Session tracking using JTI (token revocation support)
- Logout invalidation (server-side session control)
- Role-based access control (RBAC)
- Method-level security using `@PreAuthorize`
- Proper 401 vs 403 handling
- Global exception handling with REST-compliant responses

---

## 👤 Role-Based Authorization

Two roles supported:

- `ROLE_USER`
- `ROLE_ADMIN`

### Access Matrix

| Endpoint | ROLE_USER | ROLE_ADMIN |
|-----------|------------|-------------|
| Create Payment | ✅ | ✅ |
| Authorize | ✅ (own only) | ✅ |
| Capture | ✅ (own only) | ✅ |
| Refund | ❌ | ✅ |

Admin can override ownership restrictions.

---

## 🔁 Session Management (JTI-Based)

Unlike basic JWT implementations:

- Each login creates a session record
- JWT includes unique JTI
- Session stored in database
- Logout marks session inactive
- Filter validates both JWT and session

This enables **server-side token revocation**.

---

## 🗄️ Database Design

### 📦 Entities

<div align="center">

<table>
<tr>
<td valign="top" width="33%">

### 1️⃣ Users

- UUID primary key  
- Unique username  
- Encrypted password  
- Role column  

</td>

<td valign="top" width="33%">

### 2️⃣ Payments

- UUID primary key  
- Many-to-One with User  
- Status enum  
- Reference ID  
- Created / Updated timestamps  

</td>

<td valign="top" width="33%">

### 3️⃣ User Sessions

- UUID primary key  
- Linked to User  
- JTI  
- Active flag  
- Expiry tracking  

</td>
</tr>
</table>

</div>

All relationships are enforced via foreign keys.

---

## 🔄 Payment State Machine

Supported transitions:
```CREATED → AUTHORIZED → CAPTURED → REFUNDED```


Rules enforced:

- Cannot authorize unless CREATED
- Cannot capture unless AUTHORIZED
- Cannot refund unless CAPTURED
- Prevents duplicate transitions
- Logs all state changes

Ensures controlled domain behavior.

---

## 🏦 ISO 20022 XML Processing

- Accepts XML payment messages
- Parses ISO-style structure
- Extracts amount and currency
- Converts to internal Payment entity
- Creates payment via service layer

Simulates financial message ingestion.

---

## 🛡 Ownership Validation

- Users can access only their own payments
- Ownership validated in service layer
- Admin override supported

Prevents cross-user data exposure.

---

## ⚠️ Exception Handling

Global exception handler ensures proper HTTP status codes:

- `409` → Conflict
- `404` → Resource not found
- `400` → Business logic error
- `403` → Authorization denied
- `401` → Authentication failure

---

## 🧪 Testing

Integration tests implemented using:

- Spring Boot Test
- H2 in-memory database
- MockMvc

Test coverage includes:

- Registration
- Login
- Payment creation
- Authorize
- Capture
- Refund
- Role enforcement
- Session invalidation

---

# 🏗 Architecture Overview
```
Controller Layer
↓
Service Layer (Business Logic + State Machine)
↓
Repository Layer (JPA)
↓
Database
```

Security Layer:

- JWT Filter
- Method-level security
- Session validation

Exception Layer:

- Centralized REST exception mapping


---

# 🛠 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL (Development)
- H2 (Testing)
- JWT (jjwt)
- Lombok
- JUnit 5

---

# 🔑 Authentication Flow

1. Register user
2. Login
3. Receive JWT token
4. Pass token in header: ```Authorization: Bearer <token>```
5. Access protected endpoints

---

# 📈 Benefits of This Design

✔ Secure token revocation  
✔ Strong domain control with state machine  
✔ Layered authorization (Authentication + RBAC + Ownership)  
✔ Clean separation of concerns  
✔ Financial message simulation  
✔ Integration-tested  
✔ Production-ready architecture foundation  

---

# 🔮 Future Enhancements

- Refresh token implementation
- Idempotency support
- Audit logging
- Docker containerization
- Flyway/Liquibase migrations
- Rate limiting
- Swagger/OpenAPI documentation
- CI/CD pipeline

---

# 📌 Project Status

This project demonstrates:

- Secure authentication
- Server-side session control
- Role-based authorization
- Domain-driven state machine
- XML financial message parsing
- Proper database modeling
- Integration testing

Suitable for:

- Resume-level backend project
- Interview demonstrations
- Advanced Spring Security learning

---

# 📄 License

Educational and demonstration purposes.
