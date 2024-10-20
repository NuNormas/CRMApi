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
  Request Body: {"sellerId": 1, "amount": "10.00", "paymentType": "CASH" or "CARD" or "TRANSFER"}
  Example: {"sellerId": 1, "amount": "10.00", "paymentType": "CASH"}
- **GET /api/transactions/sellers/{sellerId}**: Retrieve all transactions for a specific seller.
- **GET /api/transactions/sellers/best-seller/day**: Retrieve the best seller for the current day.
- **GET /api/transactions/sellers/best-seller/month**: Retrieve the best seller for the current month.
- **GET /api/transactions/sellers/best-seller/quarter**: Retrieve the best seller for the current quarter.
- **GET /api/transactions/sellers/best-seller/year**: Retrieve the best seller for the current year.
- **GET /api/transactions/sellers/less-than**: Retrieve sellers with less than a specified amount of transactions within a given period.
  Query Parameters: amount, startDate, endDate
  Example: ?amount=30.00&startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
- **GET /api/transactions/sellers/{sellerId}/best-period**: Retrieve best period of seller, when he made the most transactions

## Instructions for Building and Running

### Prerequisites
- Java 11 or later
- Gradle 7.x or later: Ensure you have Gradle 7.x or a later version installed.
- Spring Boot 2.7.x or later
- PostgreSQL or any other database supported by Spring Boot
- Mockito for testing

### Steps to Build and Run

1. **Open Project**
   Unzip the archive with project
   
2. **Database Configuration**
Edit the src/main/resources/application.properties file to configure your database settings.
    ```text
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```
3. **Build Project**

4. **Running project**
- Run the DemoApplication in demo/src/main/kotlin/com/example/demo

### Example Usage
- **Create a New Seller**
    ```bash
    curl -X POST \
      http://localhost:8080/api/sellers \
      -H 'Content-Type: application/json' \
      -d '{"name": "John Doe", "contactInfo": "123456789"}'

- **Create a New Transaction**
    ```bash
    curl -X POST \
      http://localhost:8080/api/transactions \
      -H 'Content-Type: application/json' \
      -d '{"sellerId": 1, "amount": "10.00", "paymentType": "CASH"}'

- **Get Best Seller of the Day**
    ```bash
    curl -X GET \
      http://localhost:8080/api/transactions/best-seller/day

- **Get Sellers with Less Than a Specified Amount of Transactions**
    ```bash
    curl -X GET \
      'http://localhost:8080/api/transactions/sellers/less-than?amount=30.00&startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59'

### Dependencies
- **The project uses the following dependencies**:
-   Spring Boot Starter Web
-   Spring Boot Starter Data JPA
-   PostgreSQL
-   Mockito for testing
- **Here is an excerpt from the build.gradle file showing the dependencies**:
  ```groovy
  dependencies {
      implementation 'org.springframework.boot:spring-boot-starter-web'
      implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
      runtimeOnly("org.postgresql:postgresql")
      testImplementation 'org.mockito:mockito-core'
      // Other dependencies
  }

### Testing
- To Run test go to demo/src/test/kotlin/com/example/demo, choose the test you want to run, and run this test. If there are no errors, all tests ran correctly
