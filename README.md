# 🚀 OrderFlow API

A robust RESTful API for managing restaurant and eatery orders, built with **Java 17** and **Spring Boot**, applying **Clean Architecture** and **Domain-Driven Design (DDD)** principles.

---

## 📌 Features

- **User Authentication & Authorization**:
  - Secure JWT-based authentication.
  - Role-Based Access Control (`ADMIN`, `ATTENDANT`, `KITCHEN`, `DELIVERY`, `GUEST`).
  - First registered user automatically receives the `ADMIN` role.
- **Product Management**:
  - Full CRUD operations for menu products.
  - Restricted operations (Create, Update, Delete) reserved for `ADMIN`.
- **Order Lifecycle Management**:
  - Order creation with automatic item subtotal and total calculations.
  - Strict state machine transitions (`PENDING` ➔ `PREPARING` ➔ `OUT_FOR_DELIVERY` ➔ `COMPLETED` / `CANCELED`).
  - Role-based restrictions on status progression.
- **Global Exception Handling**:
  - Standardized JSON error responses for business rules and malformed requests.
- **API Documentation**:
  - Swagger / OpenAPI integration with Bearer JWT support.

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3** (Spring Security, Spring Data JPA, Spring Web)
- **PostgreSQL**
- **Flyway** (Database Migrations)
- **JUnit 5 & Mockito** (Unit & Integration Testing)
- **OpenAPI 3 / Swagger UI**
- **Maven**

---

## 🏗️ Architecture & Project Structure

The project follows Clean Architecture concepts to decouple core business logic from framework and infrastructure concerns:

```text
src/main/java/com/nadson/orderflow/
├── modules/
│   ├── users/        # User domain, use cases, JPA persistence, and Auth endpoints
│   ├── products/     # Product domain, use cases, JPA persistence, and Product endpoints
│   └── orders/       # Order domain aggregate, state machine, use cases, and Order endpoints
└── shared/           # Cross-cutting concerns (Security, Exception Handling, OpenAPI Config)

```

---

## 🚦 Getting Started

### Prerequisites

* **Java 17+**
* **Docker & Docker Compose** (for running PostgreSQL)
* **Git**

### Running the Application

1. **Clone the repository:**
```bash
git clone [https://github.com/njansh/OrderFlow.git](https://github.com/njansh/OrderFlow.git)
cd OrderFlow

```


2. **Start the database container:**
```bash
docker compose up -d

```


3. **Run the Spring Boot application:**
```bash
./mvnw spring-boot:run

```



The application will automatically run Flyway database migrations and start on `http://localhost:8080`.

---

## 🧪 Running Tests

To run the full suite of unit and controller integration tests:

```bash
./mvnw test

```

---

## 📖 API Documentation

Once the application is running, access the interactive Swagger UI at:
👉 `http://localhost:8080/swagger-ui.html`

Use the **Authorize** button in Swagger UI to attach your Bearer JWT token received from `POST /auth/login`.

---

## 📄 License

This project is open-source and available under the [MIT License](https://www.google.com/search?q=LICENSE).
