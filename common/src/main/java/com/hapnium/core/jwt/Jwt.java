package com.hapnium.core.jwt;

import com.hapnium.core.exception.HapJwtException;
import com.hapnium.core.jwt.models.JwtRequest;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.Key;
import java.util.Date;
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
            throw new HapJwtException(JwtConstant.SECRET_OR_ISSUER_MISSING, "Secret or Issuer cannot be null or empty");
        }
        this.secret = secret;
        this.issuer = issuer;
        this.expiration = null;
    }

    Jwt(String secret, String issuer, Long expiration) {
        if ((secret == null || secret.isEmpty()) || (issuer == null || issuer.isEmpty()) || expiration == null) {
            throw new HapJwtException(JwtConstant.SECRET_ISSUER_EXPIRATION_MISSING, "Secret, Expiration, or Issuer cannot be null or empty");
        }
        this.secret = secret;
        this.issuer = issuer;
        this.expiration = expiration;
    }

    @Contract(" -> new")
    private @NotNull Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private Claims fetch(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new HapJwtException(JwtConstant.TOKEN_EXPIRED, "JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new HapJwtException(JwtConstant.TOKEN_UNSUPPORTED, "JWT token is unsupported", e);
        } catch (MalformedJwtException e) {
            throw new HapJwtException(JwtConstant.TOKEN_MALFORMED, "JWT token is malformed", e);
        } catch (SecurityException e) {
            throw new HapJwtException(JwtConstant.SIGNATURE_INVALID, "JWT signature validation failed", e);
        } catch (IllegalArgumentException e) {
            throw new HapJwtException(JwtConstant.TOKEN_ILLEGAL, "JWT token is illegal or empty", e);
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.UNKNOWN_ERROR, "Unknown error while parsing JWT", e);
        }
    }

    private <T> T extract(String token, @NotNull Function<Claims, T> fetch) {
        return fetch.apply(fetch(token));
    }

    @Override
    public String generateToken(JwtRequest param) {
        if (param == null) {
            throw new HapJwtException(JwtConstant.JWT_PARAM_NULL, "JwtParam cannot be null");
        }

        try {
            if (param.getUseExpiration()) {
                if (expiration == null) {
                    throw new HapJwtException(JwtConstant.EXPIRATION_NOT_PROVIDED, "You must specify an expiration when initializing HapJwt");
                }

                return Jwts.builder()
                        .addClaims(param.getClaims())
                        .setSubject(param.getSubject())
                        .setIssuer(Encoders.BASE64.encode(issuer.getBytes()))
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expiration))
                        .setAudience(param.getAudience())
                        .signWith(getSigningKey())
                        .compact();
            } else {
                return Jwts.builder()
                        .addClaims(param.getClaims())
                        .setSubject(param.getSubject())
                        .setIssuer(Encoders.BASE64.encode(issuer.getBytes()))
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setAudience(param.getAudience())
                        .signWith(getSigningKey())
                        .compact();
            }
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.TOKEN_GENERATION_FAILED, "Failed to generate JWT token", e);
        }
    }

    @Override
    public Boolean isExpired(String token) {
        try {
            return extract(token, Claims::getExpiration).before(new Date());
        } catch (ExpiredJwtException e) {
            throw new HapJwtException(JwtConstant.TOKEN_EXPIRED, "JWT token has expired", e);
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.UNKNOWN_ERROR, "Failed to check if token is expired", e);
        }
    }

    @Override
    public String get(String token, String identifier) {
        try {
            return extract(token, claims -> claims.get(identifier, String.class));
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.UNKNOWN_ERROR, "Failed to extract claim '" + identifier + "' from JWT", e);
        }
    }

    @Override
    public <T> T get(String token, String identifier, Class<T> type) {
        try {
            return extract(token, claims -> claims.get(identifier, type));
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.UNKNOWN_ERROR, "Failed to extract claim '" + identifier + "' from JWT", e);
        }
    }

    @Override
    public String getSubject(String token) {
        try {
            return extract(token, Claims::getSubject);
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.UNKNOWN_ERROR, "Failed to extract subject from JWT", e);
        }
    }

    @Override
    public String getAudience(String token) {
        try {
            return extract(token, Claims::getAudience);
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.UNKNOWN_ERROR, "Failed to extract audience from JWT", e);
        }
    }

    @Override
    public Boolean isSigned(String token, String issuer) {
        try {
            return issuer.equals(new String(Decoders.BASE64.decode(extract(token, Claims::getIssuer))));
        } catch (Exception e) {
            throw new HapJwtException(JwtConstant.UNKNOWN_ERROR, "Failed to verify JWT signature issuer", e);
        }
    }
}