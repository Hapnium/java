package com.hapnium.core.jwt;

import com.hapnium.core.exception.HapJwtException;
import com.hapnium.core.jwt.models.JwtRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HapJwtTest {
    private static final String SECRET = "VGhpcyBpcyBhIHZlcnkgc2VjdXJlIHNlY3JldCBrZXkgZm9yIHRlc3Rpbmc="; // Base64-encoded
    private static final String ISSUER = "hapnium-test";
    private static final String AUDIENCE = "web-client";
    private static final Long EXPIRATION = 1000L * 60 * 60; // 1 hour

    private HapJwt hapJwt;

    @BeforeEach
    void setUp() {
        hapJwt = HapJwt.create(SECRET, ISSUER, EXPIRATION);
    }

    @Test
    void shouldGenerateAndValidateJwtToken() {
        JwtRequest param = new JwtRequest();
        param.setSubject("user123");
        param.setAudience(AUDIENCE);
        param.setClaims(Map.of("role", "admin", "tier", "premium"));
        param.setUseExpiration(true);

        String token = hapJwt.generateToken(param);
        assertNotNull(token, "Token should not be null");

        // Validate claims
        assertEquals("user123", hapJwt.getSubject(token));
        assertEquals("admin", hapJwt.get(token, "role"));
        assertEquals("premium", hapJwt.get(token, "tier"));
        assertEquals(Set.of(AUDIENCE), hapJwt.getAudience(token));
        assertFalse(hapJwt.isExpired(token));
        assertTrue(hapJwt.isSigned(token, ISSUER));
    }

    @Test
    void shouldGenerateTokenWithoutExpiration() {
        HapJwt noExpiryJwt = HapJwt.create(SECRET, ISSUER);
        JwtRequest param = new JwtRequest();
        param.setSubject("user456");
        param.setClaims(Map.of("role", "viewer"));
        param.setAudience("api");
        param.setUseExpiration(false);

        String token = noExpiryJwt.generateToken(param);
        assertNotNull(token);
        assertEquals("viewer", noExpiryJwt.get(token, "role"));
        assertEquals("user456", noExpiryJwt.getSubject(token));
    }

    @Test
    void shouldDetectExpiredToken() throws InterruptedException {
        HapJwt shortLivedJwt = HapJwt.create(SECRET, ISSUER, 1000L); // 1 second
        JwtRequest param = new JwtRequest();
        param.setSubject("userExpired");
        param.setUseExpiration(true);
        param.setClaims(Map.of());

        String token = shortLivedJwt.generateToken(param);
        Thread.sleep(1500); // Wait for jwt to expire

        assertThrows(HapJwtException.class, () -> shortLivedJwt.isExpired(token));
    }
}