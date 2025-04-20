# Telecom E-Commerce Application

A full-stack e-commerce application for a telecom company, built with Spring Boot microservices and React.

## Project Structure

- **Frontend**: React application with Material-UI
- **Backend Microservices**:
  - API Gateway
  - Eureka Service Discovery
  - Product Service
  - Cart Service
  - Discount Service
  - Payment Service
  - Notification Service

## Features

- Product catalog with filtering and sorting
- Shopping cart functionality
- Discount system
- Payment processing
- Real-time notifications
- Responsive UI

## Technology Stack

### Backend
- Java 11
- Spring Boot
- Spring Cloud (Gateway, Eureka)
- Spring Data JPA
- PostgreSQL
- Kafka
- Resilience4j

### Frontend
- React
- Redux
- Material-UI
- Axios

## Getting Started

### Prerequisites
- Java 11+
- Maven
- Node.js
- PostgreSQL
- Kafka (for notification service)

### Running the Application

1. Start the Eureka server:
```bash
cd backend/eureka-server
./mvnw spring-boot:run
```

2. Start the backend microservices (in separate terminals):
```bash
cd backend/product-service
./mvnw spring-boot:run

cd backend/cart-service
./mvnw spring-boot:run

cd backend/discount-service
./mvnw spring-boot:run

cd backend/payment-service
./mvnw spring-boot:run

cd backend/notification-service
./mvnw spring-boot:run

cd backend/api-gateway
./mvnw spring-boot:run
```

3. Start the frontend:
```bash
cd frontend
npm install
npm run dev
```

4. Access the application at http://localhost:3000

## API Documentation

- API Gateway: http://localhost:9000
- Individual services expose REST APIs at their respective ports
- Swagger documentation available at `/swagger-ui/` for each service 