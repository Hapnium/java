package com.hapnium.core.qr_code;

import com.hapnium.core.exception.HapQrCodeException;
import com.hapnium.core.qr_code.models.QrCodeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h1>Test for HapQrCode</h1>
 * This class contains unit tests for the `HapQrCode` class which tests both the functionality of
 * generating the OTP authenticator URL and generating a QR code based on provided parameters.
 */
public class HapQrCodeTest {
    private HapQrCode hapQrCode;

    /**
     * Sets up the HapQrCode instance before each test.
     */
    @BeforeEach
    public void setUp() {
        hapQrCode = new HapQrCode();
    }

    /**
     * Test for the getAuthenticatorUrl method.
     * This test checks if the URL is generated correctly given valid parameters.
     */
    @Test
    public void testGetAuthenticatorUrl() {
        // Sample valid inputs
        String secret = "JBSWY3DPEHPK3PXP";
        String account = "user@example.com";
        String issuer = "MyApp";

        // Expected OTP URL format (encoded)
        String expectedUrl = "otpauth://totp/MyApp%3Auser%40example.com?secret=JBSWY3DPEHPK3PXP&issuer=MyApp";

        // Generate the URL using the method
        String url = hapQrCode.getAuthenticatorUrl(secret, account, issuer);

        // Assert the result
        assertEquals(expectedUrl, url, "The generated URL does not match the expected one.");
    }

    /**
     * Test for the generate method.
     * This test checks if the QR code is generated correctly given valid parameters.
     */
    @Test
    public void testGenerateQrCode() {
        QrCodeRequest param = new QrCodeRequest();
        param.setUrl("https://example.com");
        param.setWidth(200);
        param.setHeight(200);
        param.setColor(0x000000);
        param.setTransparent(false);

        String qrCodeBase64 = hapQrCode.generate(param);

        assertNotNull(qrCodeBase64, "Generated QR code is null.");
        assertFalse(qrCodeBase64.isEmpty(), "Generated QR code is empty.");

        // Optional: Try to decode it to ensure it's valid Base64
        assertDoesNotThrow(() -> {
            byte[] decoded = java.util.Base64.getDecoder().decode(qrCodeBase64);
            assertTrue(decoded.length > 0, "Decoded QR code bytes are empty.");
        });

        byte[] decoded = java.util.Base64.getDecoder().decode(qrCodeBase64);

        // PNG files always start with these 8 bytes
        byte[] pngSignature = {(byte)137, 80, 78, 71, 13, 10, 26, 10};
        for (int i = 0; i < pngSignature.length; i++) {
            assertEquals(pngSignature[i], decoded[i], "Mismatch in PNG signature at byte " + i);
        }
    }

    /**
     * Test for the getAuthenticatorUrl method with invalid input.
     * This test ensures that the method throws an exception when given invalid parameters (e.g., null values).
     */
    @Test
    public void testGetAuthenticatorUrlWithInvalidInput() {
        // Test with null secret
        assertThrows(HapQrCodeException.class, () -> {
            hapQrCode.getAuthenticatorUrl(null, "user@example.com", "MyApp");
        }, "Expected exception when secret is null.");

        // Test with null account
        assertThrows(HapQrCodeException.class, () -> {
            hapQrCode.getAuthenticatorUrl("JBSWY3DPEHPK3PXP", null, "MyApp");
        }, "Expected exception when account is null.");

        // Test with null issuer
        assertThrows(HapQrCodeException.class, () -> {
            hapQrCode.getAuthenticatorUrl("JBSWY3DPEHPK3PXP", "user@example.com", null);
        }, "Expected exception when issuer is null.");
    }

    /**
     * Test for the generate method with invalid parameters.
     * This test ensures that the method throws an exception when given invalid parameters (e.g., null or empty URL).
     */
    @Test
    public void testGenerateQrCodeWithInvalidParams() {
        // Test with null URL
        QrCodeRequest invalidParam = new QrCodeRequest();
        invalidParam.setUrl(null); // Invalid URL
        invalidParam.setWidth(200);
        invalidParam.setHeight(200);

        assertThrows(HapQrCodeException.class, () -> {
            hapQrCode.generate(invalidParam);
        }, "Expected exception when URL is null.");
    }
}