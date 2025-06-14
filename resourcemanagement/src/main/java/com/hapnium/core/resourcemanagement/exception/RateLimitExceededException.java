package com.hapnium.core.resourcemanagement.exception;

import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private final RateLimitResult rateLimitResult;
    
    public RateLimitExceededException(String message) {
        super(message);
        this.rateLimitResult = null;
    }
    
    public RateLimitExceededException(String message, RateLimitResult rateLimitResult) {
        super(message);
        this.rateLimitResult = rateLimitResult;
    }
    
    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
        this.rateLimitResult = null;
    }
}