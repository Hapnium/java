package com.hapnium.core.mfa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HapMFATest {
    private HapMFA mfaService;

    @BeforeEach
    void setUp() {
        mfaService = new HapMFA();
    }

    @Test
    void testGenerateSecret() {
        String secret = mfaService.generateSecret(true);
        assertNotNull(secret, "Secret should not be null");
        assertTrue(secret.length() >= 32, "Secret should have a minimum length");
        assertTrue(secret.matches("([a-z2-7]{4} ?)+"), "Secret should be base32 and human-readable");
    }

    @Test
    void testGenerateAndValidateCode() {
        String secret = mfaService.generateSecret(true);
        String code = mfaService.getCode(secret);
        assertNotNull(code, "Code should not be null");
        assertEquals(6, code.length(), "Code should be 6 digits long");
        assertTrue(code.matches("\\d{6}"), "Code should be numeric");

        boolean isValid = mfaService.isValid(code, secret);
        assertTrue(isValid, "Generated code should be valid for the same secret");
    }

    @Test
    void testInvalidCode() {
        String secret = mfaService.generateSecret(true);
        String invalidCode = "123456";
        boolean isValid = mfaService.isValid(invalidCode, secret);
        assertFalse(isValid, "Manually entered wrong code should not be valid");
    }
}