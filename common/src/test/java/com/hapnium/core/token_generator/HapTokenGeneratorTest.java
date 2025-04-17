package com.hapnium.core.token_generator;

import com.hapnium.core.token_generator.models.TokenParam;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HapTokenGeneratorTest {
    @Test
    void testGenerateOtp_DefaultLengthAndCharacters() {
        HapTokenGenerator generator = new HapTokenGenerator();
        String otp = generator.generateOtp();

        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("[0-9]{6}"));
    }

    @Test
    void testGenerateWithOtp_CustomLength() {
        HapTokenGenerator generator = new HapTokenGenerator();
        int length = 8;
        String otp = generator.generateWithOtp(length);

        assertNotNull(otp);
        assertEquals(length, otp.length());
        assertTrue(otp.matches("[0-9]{" + length + "}"));
    }

    @Test
    void testGenerateToken_DefaultLengthAndCharacters() {
        HapTokenGenerator generator = new HapTokenGenerator();
        String token = generator.generate();

        assertNotNull(token);
        assertEquals(64, token.length());
        assertTrue(token.matches("[A-Za-z0-9]{64}"));
    }

    @Test
    void testGenerateToken_CustomLength() {
        HapTokenGenerator generator = new HapTokenGenerator();
        int length = 32;
        String token = generator.generate(length);

        assertNotNull(token);
        assertEquals(length, token.length());
        assertTrue(token.matches("[A-Za-z0-9]{" + length + "}"));
    }

    @Test
    void testGenerateToken_CustomCharactersAndLength() {
        String characters = "ABC123";
        int length = 10;
        HapTokenGenerator generator = new HapTokenGenerator();
        String token = generator.generate(characters, length);

        assertNotNull(token);
        assertEquals(length, token.length());
        assertTrue(token.matches("[ABC123]+"));
    }

    @Test
    void testWithCustomTokenParam() {
        TokenParam param = new TokenParam();
        param.setOtpCharacters("XYZ");
        param.setOtpLength(4);
        param.setTokenCharacters("123");
        param.setTokenLength(5);

        HapTokenGenerator generator = new HapTokenGenerator(param);

        String otp = generator.generateOtp();
        String token = generator.generate();

        assertEquals(4, otp.length());
        assertTrue(otp.matches("[XYZ]+"));

        assertEquals(5, token.length());
        assertTrue(token.matches("[123]+"));
    }

    @Test
    void testGenerateToken_IgnoresWhitespaceInCharacters() {
        HapTokenGenerator generator = new HapTokenGenerator();
        String token = generator.generate(" A B C 1 2 3 ", 6);

        assertNotNull(token);
        assertEquals(6, token.length());
        assertTrue(token.matches("[ABC123]+"));
    }
}