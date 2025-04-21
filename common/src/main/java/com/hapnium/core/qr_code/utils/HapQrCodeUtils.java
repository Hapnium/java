package com.hapnium.core.qr_code.utils;

import com.hapnium.core.qr_code.enums.QrCodeFormat;

/**
 * Utility class for formatting QR codes into different output representations.
 * <p>
 * Supports formatting QR codes into:
 * <ul>
 *     <li>{@code response:image/png;base64,xxxx} (RESPONSE)</li>
 *     <li>{@code data:image/png;base64,xxxx} (DATA_URI)</li>
 *     <li>Plain Base64 string without any prefix (PLAIN)</li>
 * </ul>
 */
public class HapQrCodeUtils {
    /**
     * Formats the given QR code Base64 string using the default format ({@link QrCodeFormat#RESPONSE}).
     *
     * @param qrcode the Base64-encoded QR code string
     * @return the formatted QR code string
     */
    public static String format(String qrcode) {
        return format(qrcode, QrCodeFormat.RESPONSE);
    }

    /**
     * Formats the given QR code Base64 string according to the specified {@link QrCodeFormat}.
     *
     * @param qrcode the Base64-encoded QR code string
     * @param format the desired output format
     * @return the formatted QR code string
     */
    public static String format(String qrcode, QrCodeFormat format) {
        return switch (format) {
            case RESPONSE -> String.format("response:image/png;base64,%s", qrcode);
            case DATA_URI -> String.format("data:image/png;base64,%s", qrcode);
            case PLAIN -> qrcode;
        };
    }
}