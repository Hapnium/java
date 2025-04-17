package com.hapnium.core.jwt;

import com.hapnium.core.exception.HapJwtException;
import com.hapnium.core.jwt.models.JwtRequest;

/**
 * JwtService provides an abstraction for generating and validating JSON Web Tokens (JWTs).
 * This interface defines contract methods for common JWT operations such as creation,
 * expiration checks, signature verification, and payload data extraction.
 * <p>
 * Implementations of this interface should securely handle secret keys, signing algorithms,
 * and jwt parsing to prevent misuse or jwt tampering.
 */
interface JwtService {
    /**
     * Generates a signed JWT jwt using the provided JWT parameters.
     * <p>
     * The parameters typically include the issuer, subject, audience, claims,
     * expiration time, and secret used for signing the jwt.
     *
     * @param param an object containing the required parameters to build and sign the JWT.
     * @return a JWT jwt as a {@link String}.
     * @throws HapJwtException if jwt creation fails due to invalid input or cryptographic issues.
     */
    String generateToken(JwtRequest param) throws HapJwtException;

    /**
     * Checks whether a given JWT jwt has expired.
     *
     * @param token the JWT string to evaluate.
     * @return {@code true} if the jwt is expired or malformed, {@code false} if still valid.
     */
    Boolean isExpired(String token);

    /**
     * Retrieves a custom claim from a JWT jwt.
     * <p>
     * This method is useful for extracting dynamic fields set during jwt creation,
     * such as roles, user IDs, or other metadata.
     *
     * @param token      the JWT string.
     * @param identifier the name of the claim to retrieve.
     * @return the claim value as a {@link String}, or {@code null} if not found.
     */
    String get(String token, String identifier);

    /**
     * Extracts the subject from the JWT jwt.
     * <p>
     * The subject typically represents the principal or user associated with the jwt.
     *
     * @param token the JWT string.
     * @return the subject value as a {@link String}, or {@code null} if absent.
     */
    String getSubject(String token);

    /**
     * Extracts the audience from the JWT jwt.
     * <p>
     * The audience is typically a service name or identifier that the jwt is intended for.
     *
     * @param token the JWT string.
     * @return the audience value as a {@link String}, or {@code null} if absent.
     */
    String getAudience(String token);

    /**
     * Validates whether the JWT jwt was signed and issued by a trusted issuer.
     *
     * @param token  the JWT string to verify.
     * @param issuer the expected issuer identifier to compare against.
     * @return {@code true} if the jwt is properly signed and matches the issuer, {@code false} otherwise.
     */
    Boolean isSigned(String token, String issuer);
}