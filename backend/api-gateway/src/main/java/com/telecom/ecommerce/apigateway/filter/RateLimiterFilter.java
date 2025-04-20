package com.telecom.ecommerce.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimiterFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterFilter.class);

    // Simple in-memory rate limiter
    // In a real production environment, use Redis or another distributed solution
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    
    // Allow 50 requests per minute per IP address
    private static final int MAX_REQUESTS_PER_MINUTE = 50;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        
        // Get or create counter for this IP
        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, 
            k -> new RequestCounter(System.currentTimeMillis()));
        
        // Check if we need to reset the counter (1 minute has passed)
        if (System.currentTimeMillis() - counter.getStartTime() > Duration.ofMinutes(1).toMillis()) {
            counter.reset(System.currentTimeMillis());
        }
        
        // Increment request count
        int requestCount = counter.incrementAndGet();
        
        // Check if rate limit exceeded
        if (requestCount > MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for client IP: {}", clientIp);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
    
    // Inner class to track request counts
    private static class RequestCounter {
        private int count;
        private long startTime;
        
        public RequestCounter(long startTime) {
            this.count = 0;
            this.startTime = startTime;
        }
        
        public synchronized int incrementAndGet() {
            return ++count;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public synchronized void reset(long newStartTime) {
            this.count = 0;
            this.startTime = newStartTime;
        }
    }
} 