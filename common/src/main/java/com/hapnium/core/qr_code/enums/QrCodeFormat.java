package com.hapnium.core.qr_code.enums;

public enum QrCodeFormat {
    /**
     * Format the QR code with a "response" prefix.
     * <p>Example: {@code response:image/png;base64,xxxx}</p>
     * <p>Used for internal or API responses that expect a "response:" schema.</p>
     */
    RESPONSE,

    /**
     * Format the QR code as a standard Data URI.
     * <p>Example: {@code data:image/png;base64,xxxx}</p>
     * <p>Commonly used for embedding images directly in HTML or web views.</p>
     */
    DATA_URI,

    /**
     * Return the raw Base64-encoded QR code string.
     * <p>Example: {@code xxxx}</p>
     * <p>Useful when you only need the pure Base64 string without any prefix.</p>
     */
    PLAIN
}