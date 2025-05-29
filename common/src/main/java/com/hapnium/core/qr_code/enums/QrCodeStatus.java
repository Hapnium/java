package com.hapnium.core.qr_code.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>QrCode Constants</h1>
 * <p>
 * Defines all exception codes and messages related to QR Code operations.
 * Each code helps identify the specific issue and makes error tracking easier.
 * </p>
 */
@Getter
@AllArgsConstructor
public enum QrCodeStatus {
    /**
     * QR-101: Parameters missing for QR Code generation.
     * Thrown when `QrCodeParam` is null.
     */
    MISSING_PARAMETER("QR-101"),

    /**
     * QR-102: Missing secret, account, or issuer for authenticator URL generation.
     * Thrown when any required field is null or empty.
     */
    MISSING_KEY("QR-102"),

    /**
     * QR-103: Failure during QR code image generation.
     * Thrown if ZXing fails to create the BitMatrix or BufferedImage.
     */
    IMAGE_GENERATION_FAILED("QR-103"),

    /**
     * QR-104: Failure adding logo to QR code.
     * Thrown if logo URL is invalid, image reading fails, or drawing fails.
     */
    LOGO_ADDITION_FAILED("QR-104"),

    /**
     * QR-105: Qr Code generation failed due to unknown reasons.
     * Catch-all for failed generation exceptions.
     */
    GENERATION_FAILED("QR-105");

    private final String code;
}