package com.telecom.ecommerce.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.FallbackHeadersGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> errorRoutes() {
        return RouterFunctions.route(RequestPredicates.path("/fallback"),
                request -> {
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
                    responseBody.put("message", "The service is currently unavailable. Please try again later.");
                    
                    return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(responseBody);
                });
    }

    @Bean
    public GatewayFilter fallbackHeadersFilter() {
        return new FallbackHeadersGatewayFilterFactory().apply(new FallbackHeadersGatewayFilterFactory.Config());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .circuitBreaker(config -> config
                                        .setName("productCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, 
                                                    HttpStatus.BAD_GATEWAY,
                                                    HttpStatus.SERVICE_UNAVAILABLE))
                                .addRequestHeader("X-Gateway-Request", "true")
                                .addResponseHeader("X-Gateway-Response", "true"))
                        .uri("lb://product-service"))
                .route("cart-service", r -> r
                        .path("/api/cart/**")
                        .filters(f -> f
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .circuitBreaker(config -> config
                                        .setName("cartCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(3))
                        .uri("lb://cart-service"))
                .route("discount-service", r -> r
                        .path("/api/discounts/**")
                        .filters(f -> f
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .circuitBreaker(config -> config
                                        .setName("discountCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(3))
                        .uri("lb://discount-service"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .circuitBreaker(config -> config
                                        .setName("paymentCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(3))
                        .uri("lb://payment-service"))
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .circuitBreaker(config -> config
                                        .setName("notificationCircuitBreaker")
                                        .setFallbackUri("forward:/fallback"))
                                .retry(3))
                        .uri("lb://notification-service"))
                .route("debug-route", r -> r
                        .path("/api/debug/**")
                        .uri("http://localhost:9000"))
                .build();
    }
    
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(10))
                        .build())
                .build());
    }
} 