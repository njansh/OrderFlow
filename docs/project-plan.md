# Order Management System

## MVP Goal

Allow attendants, kitchen staff and delivery staff to manage orders through their entire lifecycle.

## Roles

- ADMIN
- ATTENDANT
- KITCHEN
- DELIVERY
- GUEST

Newly registered users start as GUEST and must be promoted by an ADMIN before receiving operational permissions.

## Order Status

- PENDING
- PREPARING
- OUT_FOR_DELIVERY
- COMPLETED

## Backend Progress

### Setup & Infrastructure
- [x] #1 Setup Spring Boot Project
- [x] #2 Configure PostgreSQL
- [x] #3 Configure Flyway
- [x] #4 Configure Spring Security

### Authentication
- [x] #5 Create User Entity
- [x] #6 Create Users Migration
- [x] #7 Implement Signup
- [x] #8 Implement Login
- [x] #9 Implement Current User Endpoint

### Products
- [x] #10 Create Product Entity
- [x] #11 Create Products Migration
- [x] #12 Create Product
- [x] #13 List Products
- [x] #14 Get Product By Id
- [x] #15 Update Product
- [x] #16 Delete Product

### Orders
- [x] #17 Create Order Entity
- [x] #18 Create OrderItem Entity
- [x] #19 Create Orders Migration
- [x] #20 Create Order Items Migration
- [x] #21 Implement Create Order
- [x] #22 Implement List Orders
- [x] #23 Implement Get Order
- [x] #24 Implement Orders By Status

### Order Workflow
- [x] #25 Create Order Status Enum
- [x] #26 Implement Update Order Status
- [x] #27 Validate Status Transitions

### Authorization
- [x] #28 Create Role Enum
- [x] #29 Restrict Product Management To Admin
- [x] #30 Restrict Status Changes By Role

### Error Handling
- [x] #31 Create Custom Exceptions
- [x] #32 Create Global Exception Handler

### Testing
- [x] #33 Unit Tests Authentication
- [x] #34 Unit Tests Products
- [x] #35 Unit Tests Orders
- [x] #36 Unit Tests Status Validation
- [x] #37 Controller Tests

### Documentation
- [x] #38 Configure OpenAPI / Swagger
- [x] #39 Document Endpoints
- [x] #40 Create README

### Final Hardening
- [x] #41 Code Review
- [x] #42 Refactor Duplicated Code
- [x] #43 Verify Test Coverage > 80%
- [x] #44 MVP Release v1.0
