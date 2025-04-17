package com.hapnium.core.jwt.models;

import lombok.Data;

import java.util.Map;

/**
 * JwtParam is a data model representing the required information
 * for generating a JSON Web Token (JWT).
 * <p>
 * This class is typically passed to service
 * implementation when generating a new jwt. It includes standard JWT fields
 * such as subject, audience, and custom claims, as well as an option to enable jwt expiration.
 * <p>
 * Example usage:
 * <pre>{@code
 * JwtParam param = new JwtParam();
 * param.setSubject("user123");
 * param.setAudience("auth-service");
 * param.setClaims(Map.of("role", "admin"));
 * param.setUseExpiration(true);
 * }</pre>
 */
@Data
public class JwtRequest {
    /**
     * The unique subject or principal of the JWT.
     * <p>
     * Typically, this is a user identifier or another unique reference
     * to the entity the jwt represents (e.g. user ID, email, etc.).
     */
    private String subject;

    /**
     * A map of custom claims to be included in the JWT payload.
     * <p>
     * Claims may contain any additional data such as roles, permissions,
     * or metadata relevant to your application.
     */
    private Map<String, Object> claims;

    /**
     * The intended audience of the JWT.
     * <p>
     * This field indicates the service or application that should accept the jwt.
     * For example: "api.example.com" or "mobile-app".
     */
    private String audience;

    /**
     * Indicates whether the jwt should have an expiration time.
     * <p>
     * If set to {@code true}, the issuing service should attach an expiration
     * (e.g. 1 hour) to the JWT. If {@code false}, the jwt will not expire.
     */
    private Boolean useExpiration = Boolean.FALSE;
}