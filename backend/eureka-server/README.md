# Eureka Server for Telecom E-commerce

This is the Eureka Service Discovery Server for the Telecom E-commerce microservices architecture.

## Purpose

The Eureka Server provides the following functionality:
- Service registration: Allows microservices to register themselves
- Service discovery: Allows microservices to locate other services without hardcoding URLs
- Load balancing: Provides basic load balancing capabilities
- Health monitoring: Monitors the health of registered services

## Configuration

The Eureka Server runs on port `8761` by default and is configured not to register itself as a client.

Key configuration properties:
- `eureka.client.register-with-eureka=false` - Prevents the server from registering with itself
- `eureka.client.fetch-registry=false` - Prevents the server from fetching registry information
- `eureka.server.enable-self-preservation=false` - Disables self-preservation mode for development

## Usage

### Starting the Server

The server can be started using Maven:

```bash
mvn spring-boot:run
```

### Accessing the Dashboard

Once running, you can access the Eureka Dashboard at:

```
http://localhost:8761
```

### Client Configuration

Microservices that need to register with Eureka should include this configuration in their `application.properties`:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
``` 