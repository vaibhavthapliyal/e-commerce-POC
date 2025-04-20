package com.telecom.ecommerce.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@RestController
class DebugController {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @GetMapping("/api/debug")
    public Map<String, String> debug() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "API Gateway is working");
        return response;
    }
    
    @GetMapping("/api/debug/services")
    public Map<String, Object> services() {
        Map<String, Object> response = new HashMap<>();
        List<String> services = discoveryClient.getServices();
        
        response.put("services", services);
        
        Map<String, List<ServiceInstance>> instances = new HashMap<>();
        for (String service : services) {
            instances.put(service, discoveryClient.getInstances(service));
        }
        
        response.put("instances", instances);
        return response;
    }
} 