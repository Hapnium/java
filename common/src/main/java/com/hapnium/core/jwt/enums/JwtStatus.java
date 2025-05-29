package com.hapnium.core.jwt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Contains all the exception codes related to JWT handling.
 * <p>
 * Each code uniquely identifies a specific JWT-related error scenario.
 */
@Getter
@AllArgsConstructor
public enum JwtStatus {
    /** Secret or Issuer provided is null or empty. */
    SECRET_OR_ISSUER_MISSING("JWT-001"),

    /** Secret, Issuer, or Expiration is missing when required. */
    SECRET_ISSUER_EXPIRATION_MISSING("JWT-002"),

    /** Provided JwtParam is null during token generation. */
    JWT_PARAM_NULL("JWT-003"),

    /** Expiration missing when JwtParam specifies to use expiration. */
    EXPIRATION_NOT_PROVIDED("JWT-004"),

    /** Error occurred while generating the JWT token. */
    TOKEN_GENERATION_FAILED("JWT-005"),

    /** JWT token has expired. */
    TOKEN_EXPIRED("JWT-111"),

    /** JWT token format is unsupported. */
    TOKEN_UNSUPPORTED("JWT-112"),

    /** JWT token structure is malformed. */
    TOKEN_MALFORMED("JWT-113"),

    /** JWT signature validation failed. */
    SIGNATURE_INVALID("JWT-114"),

    /** JWT token is illegal or empty. */
    TOKEN_ILLEGAL("JWT-115"),

    /** Unknown internal error while processing JWT. */
    UNKNOWN_ERROR("JWT-999");

    private final String value;
}