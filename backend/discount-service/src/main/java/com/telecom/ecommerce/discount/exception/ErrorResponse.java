package com.telecom.ecommerce.discount.exception;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    
    // No-args constructor
    public ErrorResponse() {
    }
    
    // Manual constructor
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    // Manual getter methods
    public int getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    // Manual setter methods
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 