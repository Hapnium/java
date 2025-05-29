package com.hapnium.core.jwt;

import com.hapnium.core.exception.HapJwtException;
import com.hapnium.core.jwt.models.JwtRequest;

import java.util.Set;

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
     * Generates a signed JWT using the provided JWT parameters.
     * <p>
     * The parameters typically include the issuer, subject, audience, claims,
     * expiration time, and secret used for signing the jwt.
     *
     * @param param an object containing the required parameters to build and sign the JWT.
     * @return a JWT as a {@link String}.
     * @throws HapJwtException if jwt creation fails due to invalid input or cryptographic issues.
     */
    String generateToken(JwtRequest param) throws HapJwtException;

    /**
     * Checks whether a given JWT has expired.
     *
     * @param token the JWT string to evaluate.
     * @return {@code true} if the jwt is expired or malformed, {@code false} if still valid.
     */
    Boolean isExpired(String token);

    /**
     * Retrieves a custom claim from a JWT.
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
     * Retrieves a custom claim from a JWT.
     * <p>
     * This method is useful for extracting dynamic fields set during jwt creation,
     * such as roles, user IDs, or other metadata.
     *
     * @param type The {@link Class} type to return
     * @param token      the JWT string.
     * @param identifier the name of the claim to retrieve.
     * @return the claim value as a {@link String}, or {@code null} if not found.
     */
    <T> T get(String token, String identifier, Class<T> type);

    /**
     * Extracts the subject from the JWT.
     * <p>
     * The subject typically represents the principal or user associated with the jwt.
     *
     * @param token the JWT string.
     * @return the subject value as a {@link String}, or {@code null} if absent.
     */
    String getSubject(String token);

    /**
     * Extracts the audience from the JWT.
     * <p>
     * The audience is typically a service name or identifier that the jwt is intended for.
     *
     * @param token the JWT string.
     * @return the audience value as a set of {@link String}, or {@code null} if absent.
     */
    Set<String> getAudience(String token);

    /**
     * Validates whether the JWT was signed and issued by a trusted issuer.
     *
     * @param token  the JWT string to verify.
     * @param issuer the expected issuer identifier to compare against.
     * @return {@code true} if the jwt is properly signed and matches the issuer, {@code false} otherwise.
     */
    Boolean isSigned(String token, String issuer);
}