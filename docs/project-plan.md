# Order Management System

## MVP Goal

Allow attendants, kitchen staff and delivery staff to manage orders through their entire lifecycle.

## Roles

- ADMIN
- ATTENDANT
- KITCHEN
- DELIVERY

## Order Status

- PENDING
- PREPARING
- OUT_FOR_DELIVERY
- COMPLETED

## Backend Progress

### Setup & Infrastructure
- [x] #1 Setup Spring Boot Project
- [x] #2 Configure PostgreSQL
- [ ] #3 Configure Flyway
- [ ] #4 Configure Spring Security

### Authentication
- [ ] #5 Create User Entity
- [ ] #6 Create Users Migration
- [ ] #7 Implement Signup
- [ ] #8 Implement Login
- [ ] #9 Implement Current User Endpoint

### Products
- [ ] #10 Create Product Entity
- [ ] #11 Create Products Migration
- [ ] #12 Create Product
- [ ] #13 List Products
- [ ] #14 Get Product By Id
- [ ] #15 Update Product
- [ ] #16 Delete Product

### Orders
- [ ] #17 Create Order Entity
- [ ] #18 Create OrderItem Entity
- [ ] #19 Create Orders Migration
- [ ] #20 Create Order Items Migration
- [ ] #21 Implement Create Order
- [ ] #22 Implement List Orders
- [ ] #23 Implement Get Order
- [ ] #24 Implement Orders By Status

### Order Workflow
- [ ] #25 Create Order Status Enum
- [ ] #26 Implement Update Order Status
- [ ] #27 Validate Status Transitions

### Authorization
- [ ] #28 Create Role Enum
- [ ] #29 Restrict Product Management To Admin
- [ ] #30 Restrict Status Changes By Role

### Error Handling
- [ ] #31 Create Custom Exceptions
- [ ] #32 Create Global Exception Handler

### Testing
- [ ] #33 Unit Tests Authentication
- [ ] #34 Unit Tests Products
- [ ] #35 Unit Tests Orders
- [ ] #36 Unit Tests Status Validation
- [ ] #37 Controller Tests

### Documentation
- [ ] #38 Configure OpenAPI / Swagger
- [ ] #39 Document Endpoints
- [ ] #40 Create README

### Final Hardening
- [ ] #41 Code Review
- [ ] #42 Refactor Duplicated Code
- [ ] #43 Verify Test Coverage > 80%
- [ ] #44 MVP Release v1.0