# API Gateway Service

## Overview
The API Gateway service is the entry point for all client requests to the Telecom E-commerce application's microservices. It provides a unified interface for frontend applications and handles cross-cutting concerns like routing, load balancing, rate limiting, and more.

## Features
- **Request Routing**: Routes requests to appropriate microservices
- **Load Balancing**: Distributes traffic among service instances
- **Rate Limiting**: Protects services from excessive requests
- **Circuit Breaking**: Prevents cascading failures
- **Logging**: Logs all incoming requests and responses
- **Error Handling**: Provides standardized error responses
- **Service Discovery**: Integrates with Eureka for dynamic service discovery

## Exposed Services
The API Gateway exposes the following services:

- **Product Service**: `/api/products/**`
- **Cart Service**: `/api/cart/**`
- **Payment Service**: `/api/payments/**`
- **Discount Service**: `/api/discounts/**`
- **Notification Service**: `/api/notifications/**`

## Health and Metrics
The gateway provides endpoints for health checks and metrics:

- **Health Check**: `/health`
- **Service List**: `/services`
- **Actuator Endpoints**: `/actuator/**`

## Resilience Features
- Circuit breakers for failing services
- Automatic retries for transient failures
- Rate limiting to prevent abuse
- Request size limiting

## Configuration
The gateway configuration is in `application.properties` and `application.yml`. Key settings include:

- Service routes
- Rate limits
- Circuit breaker parameters
- Timeout settings
- CORS configuration

## Development
To run the API Gateway locally:

```bash
cd backend/api-gateway
mvn spring-boot:run
```

The gateway will start on port 9000 by default. 