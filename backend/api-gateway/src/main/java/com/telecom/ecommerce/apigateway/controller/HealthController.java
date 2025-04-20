package com.telecom.ecommerce.apigateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class HealthController {

    private final DiscoveryClient discoveryClient;
    
    @Autowired
    public HealthController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> services() {
        Map<String, Object> response = new HashMap<>();
        
        List<String> serviceNames = discoveryClient.getServices();
        Map<String, List<String>> serviceDetails = serviceNames.stream()
                .collect(Collectors.toMap(
                        serviceName -> serviceName,
                        serviceName -> discoveryClient.getInstances(serviceName).stream()
                                .map(ServiceInstance::getUri)
                                .map(Object::toString)
                                .collect(Collectors.toList())
                ));
        
        response.put("services", serviceDetails);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 