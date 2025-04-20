package com.telecom.ecommerce.discount.exception;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;
    
    public ValidationErrorResponse() {
        super();
    }
    
    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
        super(status, message, timestamp);
        this.errors = errors;
    }
    
    // Manual getter method
    public Map<String, String> getErrors() {
        return errors;
    }
    
    // Manual setter method
    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
} 