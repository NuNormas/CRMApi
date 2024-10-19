# CRMApi

# Transaction and Seller Management API

This project is a RESTful API designed to manage transactions and sellers. It provides endpoints for creating, reading, updating, and deleting sellers and transactions, as well as calculating best sellers based on transaction data.

## Functionality

### Seller Endpoints
- **GET /api/sellers**: Retrieve a list of all sellers.
- **GET /api/sellers/{id}**: Retrieve a seller by ID.
- **POST /api/sellers**: Create a new seller.
  Request Body: {"name": "Seller Name", "contactInfo": "Contact Information"}
  Example: {"name": "John Doe", "contactInfo": "123456789"}
- **PUT /api/sellers/{id}**: Update an existing seller.
  Request Body: {"name": "Updated Seller Name", "contactInfo": "Updated Contact Information"}
  Example: {"name": "Jane Smith", "contactInfo": "987654321"}
- **DELETE /api/sellers/{id}**: Delete a seller by ID.
- **GET /api/sellers**: Retrieve a list of all sellers.

### Transaction Endpoints
- **GET /api/transactions**: Retrieve a list of all transactions.
- **GET /api/transactions/{id}**: Retrieve a transaction by ID.
- **POST /api/transactions**: Create a new transaction.
- **GET /api/transactions/sellers/{sellerId}**: Retrieve all transactions for a specific seller.
- **GET /api/transactions/best-seller/day**: Retrieve the best seller for the current day.
- **GET /api/transactions/best-seller/month**: Retrieve the best seller for the current month.
- **GET /api/transactions/best-seller/quarter**: Retrieve the best seller for the current quarter.
- **GET /api/transactions/best-seller/year**: Retrieve the best seller for the current year.
- **GET /api/transactions/sellers/less-than**: Retrieve sellers with less than a specified amount of transactions within a given period.

## Instructions for Building and Running

### Prerequisites
- Java 11 or later
- Maven 3.6 or later
- Spring Boot 2.7.x or later
- MySQL or any other database supported by Spring Boot
- Mockito for testing

### Steps to Build and Run

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-repo/transaction-seller-api.git
