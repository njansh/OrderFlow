# Domain Model

## User

Represents a system user.

Attributes:

- id
- name
- email
- password
- role

Roles:

- ADMIN
- ATTENDANT
- KITCHEN
- DELIVERY

---

## Product

Represents an item available for sale.

Attributes:

- id
- name
- price

---

## Order

Represents a customer order.

Attributes:

- id
- status
- totalAmount
- createdAt

Status:

- PENDING
- PREPARING
- OUT_FOR_DELIVERY
- COMPLETED

---

## OrderItem

Represents a product inside an order.

Attributes:

- id
- product
- quantity
- unitPrice