# MVP - OrderFlow

## Problem

Many small businesses manage orders manually, which can lead to duplicated work, lost orders, communication failures between teams, and delivery mistakes.

## Solution

A system that allows attendants, kitchen staff, and delivery staff to manage orders through their entire lifecycle.

## Users

### ADMIN
Manages products and users.

### ATTENDANT
Creates customer orders.

### KITCHEN
Updates orders to PREPARING.

### DELIVERY
Updates orders to OUT_FOR_DELIVERY and COMPLETED.

## Order Flow

PENDING
↓
PREPARING
↓
OUT_FOR_DELIVERY
↓
COMPLETED

## MVP Features

- User authentication
- Product management
- Order creation
- Order listing
- Order details
- Order status update
- Role-based authorization

## Out of Scope

- Payments
- Dashboard
- Reports
- Notifications
- RabbitMQ
- Redis
- Mobile App