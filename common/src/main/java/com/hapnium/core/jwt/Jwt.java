package com.hapnium.core.jwt;

import com.hapnium.core.exception.HapJwtException;
import com.hapnium.core.jwt.enums.JwtStatus;
import com.hapnium.core.jwt.models.JwtRequest;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

/**
 * Internal implementation of the {@link JwtService} used by {@link HapJwt}.
 */
class Jwt implements JwtService {
    private final String secret;
    private final String issuer;
    private final Long expiration;

    Jwt(String secret, String issuer) {
        if ((secret == null || secret.isEmpty()) || (issuer == null || issuer.isEmpty())) {
            throw new HapJwtException(JwtStatus.SECRET_OR_ISSUER_MISSING, "Secret or Issuer cannot be null or empty");
        }

        this.secret = secret;
        this.issuer = issuer;
        this.expiration = null;
    }

    Jwt(String secret, String issuer, Long expiration) {
        if ((secret == null || secret.isEmpty()) || (issuer == null || issuer.isEmpty()) || expiration == null) {
            throw new HapJwtException(JwtStatus.SECRET_ISSUER_EXPIRATION_MISSING, "Secret, Expiration, or Issuer cannot be null or empty");
        }

        this.secret = secret;
        this.issuer = issuer;
        this.expiration = expiration;
    }

    @Contract(" -> new")
    private @NotNull SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private Claims fetch(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new HapJwtException(JwtStatus.TOKEN_EXPIRED, "JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new HapJwtException(JwtStatus.TOKEN_UNSUPPORTED, "JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            throw new HapJwtException(JwtStatus.TOKEN_MALFORMED, "JWT token is malformed", e);
        } catch (SecurityException e) {
            throw new HapJwtException(JwtStatus.SIGNATURE_INVALID, "JWT signature validation failed", e);
        } catch (IllegalArgumentException e) {
            throw new HapJwtException(JwtStatus.TOKEN_ILLEGAL, "JWT token is illegal or empty", e);
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.UNKNOWN_ERROR, "Unknown error while parsing JWT", e);
        }
    }

    private <T> T extract(String token, @NotNull Function<Claims, T> fetch) {
        return fetch.apply(fetch(token));
    }

    @Override
    public String generateToken(JwtRequest param) {
        if (param == null) {
            throw new HapJwtException(JwtStatus.JWT_PARAM_NULL, "JwtParam cannot be null");
        }

        try {
            JwtBuilder builder = Jwts.builder()
                    .claims().add(param.getClaims()).and()
                    .subject(param.getSubject())
                    .issuer(Encoders.BASE64.encode(issuer.getBytes()))
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .audience().add(param.getAudience()).and();

            if (param.getUseExpiration()) {
                if (expiration == null) {
                    throw new HapJwtException(JwtStatus.EXPIRATION_NOT_PROVIDED, "You must specify an expiration when initializing HapJwt");
                }

                builder.expiration(new Date(System.currentTimeMillis() + expiration));
            }

            return builder.signWith(getSigningKey()).compact();
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.TOKEN_GENERATION_FAILED, "Failed to generate JWT token", e);
        }
    }

    @Override
    public Boolean isExpired(String token) {
        try {
            return extract(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) {
            throw new HapJwtException(JwtStatus.TOKEN_EXPIRED, "JWT token has expired", e);
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.UNKNOWN_ERROR, "Failed to check if token is expired", e);
        }
    }

    @Override
    public String get(String token, String identifier) {
        try {
            return extract(token, claims -> claims.get(identifier, String.class));
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.UNKNOWN_ERROR, "Failed to extract claim '" + identifier + "' from JWT", e);
        }
    }

    @Override
    public <T> T get(String token, String identifier, Class<T> type) {
        try {
            return extract(token, claims -> claims.get(identifier, type));
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.UNKNOWN_ERROR, "Failed to extract claim '" + identifier + "' from JWT", e);
        }
    }

    @Override
    public String getSubject(String token) {
        try {
            return extract(token, Claims::getSubject);
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.UNKNOWN_ERROR, "Failed to extract subject from JWT", e);
        }
    }

    @Override
    public Set<String> getAudience(String token) {
        try {
            return extract(token, Claims::getAudience);
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.UNKNOWN_ERROR, "Failed to extract audience from JWT", e);
        }
    }

    @Override
    public Boolean isSigned(String token, String issuer) {
        try {
            return issuer.equals(new String(Decoders.BASE64.decode(extract(token, Claims::getIssuer))));
        } catch (Exception e) {
            throw new HapJwtException(JwtStatus.UNKNOWN_ERROR, "Failed to verify JWT signature issuer", e);
        }
    }
}