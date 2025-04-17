package com.hapnium.core.qr_code.models;

import lombok.Data;

/**
 * QrCodeParam represents the parameters required to generate a customized QR code.
 * <p>
 * This class encapsulates the essential input values for QR code rendering,
 * including dimensions, URL content, colors, and optional visual enhancements like a logo.
 * It is typically used with a service responsible for generating or returning QR code images.
 * <p>
 * Example usage:
 * <pre>{@code
 * QrCodeParam param = new QrCodeParam();
 * param.setUrl("https://example.com");
 * param.setWidth(300);
 * param.setHeight(300);
 * param.setColor(0x000000); // black
 * param.setTransparent(false);
 * param.setLogo("classpath:/static/logo.png");
 * }</pre>
 */
@Data
public class QrCodeRequest {
    /**
     * The URL or text to be encoded into the QR code.
     * <p>
     * This is typically a web link or any string content the QR code should represent.
     */
    private String url;

    /**
     * The height (in pixels) of the QR code image.
     * <p>
     * Defines the vertical size of the generated QR code.
     */
    private Integer height;

    /**
     * The width (in pixels) of the QR code image.
     * <p>
     * Defines the horizontal size of the generated QR code.
     */
    private Integer width;

    /**
     * The color of the QR code elements (in ARGB or hexadecimal integer format).
     * <p>
     * Example: {@code 0xFF000000} for opaque black.
     */
    private Integer color;

    /**
     * Flag indicating whether the QR code background should be transparent.
     * <p>
     * When set to {@code true}, the background of the QR image will be transparent,
     * otherwise it will use a default background color (typically white).
     */
    private Boolean transparent;

    /**
     * Path to a logo image to embed in the center of the QR code.
     * <p>
     * This can be a classpath location (e.g., {@code "classpath:/logo.png"}) or a URL.
     * If null or empty, no logo will be applied.
     */
    private String logo;
}