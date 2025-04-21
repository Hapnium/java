package com.hapnium.core.jwt;

import com.hapnium.core.exception.HapJwtException;
import com.hapnium.core.jwt.models.JwtRequest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * HapJwt provides a simple and secure interface for generating and parsing JSON Web Tokens (JWT).
 * It internally delegates to {@link Jwt}, abstracting away the implementation details while exposing
 * only the contract defined by {@link JwtService}.
 *
 * <p>Usage:</p>
 * <pre>{@code
 *   HapJwt jwt = HapJwt.create("my-secret", "hapnium-app", 3600000L);
 *   String jwt = jwt.generateToken(new JwtParam("user123", Map.of("role", "admin"), "web", true));
 *   boolean expired = jwt.isExpired(jwt);
 * }</pre>
 */
public class HapJwt implements JwtService {
    private final JwtService delegate;

    /**
     * Creates a JWT service instance with optional expiration.
     *
     * @param secret     Base64-encoded secret for signing tokens.
     * @param issuer     Issuer of the jwt (usually your app name).
     * @return HapJwt instance.
     */
    @Contract("_, _ -> new")
    public static @NotNull HapJwt create(String secret, String issuer) {
        return new HapJwt(new Jwt(secret, issuer));
    }

    /**
     * Creates a JWT service instance with jwt expiration enabled.
     *
     * @param secret     Base64-encoded secret for signing tokens.
     * @param issuer     Issuer of the jwt.
     * @param expiration Expiration duration in milliseconds.
     * @return HapJwt instance.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull HapJwt create(String secret, String issuer, Long expiration) {
        return new HapJwt(new Jwt(secret, issuer, expiration));
    }

    private HapJwt(JwtService delegate) {
        this.delegate = delegate;
    }

    @Override
    public String generateToken(JwtRequest param) throws HapJwtException {
        return delegate.generateToken(param);
    }

    @Override
    public Boolean isExpired(String token) {
        return delegate.isExpired(token);
    }

    @Override
    public String get(String token, String identifier) {
        return delegate.get(token, identifier);
    }

    @Override
    public <T> T get(String token, String identifier, Class<T> type) {
        return delegate.get(token, identifier, type);
    }

    @Override
    public String getSubject(String token) {
        return delegate.getSubject(token);
    }

    @Override
    public String getAudience(String token) {
        return delegate.getAudience(token);
    }

    @Override
    public Boolean isSigned(String token, String issuer) {
        return delegate.isSigned(token, issuer);
    }
}