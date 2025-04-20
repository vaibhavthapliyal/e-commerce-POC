package com.telecom.ecommerce.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(-1)
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Error occurred during request processing: {}", ex.getMessage(), ex);
        
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            return createErrorResponse(response, HttpStatus.SERVICE_UNAVAILABLE, "Service is not available");
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            response.setStatusCode(responseStatusException.getStatus());
            return createErrorResponse(response, responseStatusException.getStatus(), responseStatusException.getReason());
        }
        
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return createErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }
    
    private Mono<Void> createErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        try {
            byte[] responseBytes = objectMapper.writeValueAsBytes(errorResponse);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBytes)));
        } catch (JsonProcessingException e) {
            log.error("Error writing error response as JSON", e);
            return response.writeWith(Mono.just(response.bufferFactory()
                    .wrap(("Error: " + message).getBytes())));
        }
    }
} 