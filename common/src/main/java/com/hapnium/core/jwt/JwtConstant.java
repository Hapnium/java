package com.hapnium.core.jwt;

/**
 * Contains all the exception codes related to JWT handling.
 * <p>
 * Each code uniquely identifies a specific JWT-related error scenario.
 */
public final class JwtConstant {
    /** Secret or Issuer provided is null or empty. */
    public static final String SECRET_OR_ISSUER_MISSING = "JWT-001";

    /** Secret, Issuer, or Expiration is missing when required. */
    public static final String SECRET_ISSUER_EXPIRATION_MISSING = "JWT-002";

    /** Provided JwtParam is null during token generation. */
    public static final String JWT_PARAM_NULL = "JWT-003";

    /** Expiration missing when JwtParam specifies to use expiration. */
    public static final String EXPIRATION_NOT_PROVIDED = "JWT-004";

    /** Error occurred while generating the JWT token. */
    public static final String TOKEN_GENERATION_FAILED = "JWT-005";

    /** JWT token has expired. */
    public static final String TOKEN_EXPIRED = "JWT-111";

    /** JWT token format is unsupported. */
    public static final String TOKEN_UNSUPPORTED = "JWT-112";

    /** JWT token structure is malformed. */
    public static final String TOKEN_MALFORMED = "JWT-113";

    /** JWT signature validation failed. */
    public static final String SIGNATURE_INVALID = "JWT-114";

    /** JWT token is illegal or empty. */
    public static final String TOKEN_ILLEGAL = "JWT-115";

    /** Unknown internal error while processing JWT. */
    public static final String UNKNOWN_ERROR = "JWT-999";
}