package com.hapnium.core.qr_code;

/**
 * <h1>QrCode Constants</h1>
 * <p>
 * Defines all exception codes and messages related to QR Code operations.
 * Each code helps identify the specific issue and makes error tracking easier.
 * </p>
 */
public final class QrCodeConstant {
    /**
     * QR-101: Parameters missing for QR Code generation.
     * Thrown when `QrCodeParam` is null.
     */
    public static final String MISSING_PARAMETER = "QR-101";

    /**
     * QR-102: Missing secret, account, or issuer for authenticator URL generation.
     * Thrown when any required field is null or empty.
     */
    public static final String MISSING_KEY = "QR-102";

    /**
     * QR-103: Failure during QR code image generation.
     * Thrown if ZXing fails to create the BitMatrix or BufferedImage.
     */
    public static final String IMAGE_GENERATION_FAILED = "QR-103";

    /**
     * QR-104: Failure adding logo to QR code.
     * Thrown if logo URL is invalid, image reading fails, or drawing fails.
     */
    public static final String LOGO_ADDITION_FAILED = "QR-104";

    /**
     * QR-105: Qr Code generation failed due to unknown reasons.
     * Catch-all for failed generation exceptions.
     */
    public static final String GENERATION_FAILED = "QR-105";
}