package com.telecom.ecommerce.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    // Kafka consumer configurations will be automatically picked up from application.properties
} 