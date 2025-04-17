package com.hapnium.core.qr_code;

import com.hapnium.core.exception.HapQrCodeException;
import com.hapnium.core.qr_code.models.QrCodeRequest;

/**
 * <h1>HapQrCode</h1>
 * The `HapQrCode` class provides a simplified and abstracted interface for generating QR codes.
 * It delegates the core QR code generation functionality to the `QrCode` class while allowing
 * for the possibility of further extensions and abstractions in the future.
 * This class implements the `QrCodeService` interface and provides methods to:
 * <ul>
 *     <li>Generate a URL for use with OTP-based authenticator apps.</li>
 *     <li>Generate QR codes based on customizable parameters.</li>
 * </ul>
 */
public class HapQrCode implements QrCodeService {
    // Delegate to the actual implementation of QR code functionality
    private final QrCodeService delegate;

    /**
     * Constructor that initializes the delegate instance.
     * This constructor sets up the internal `QrCode` instance to handle QR code generation.
     */
    public HapQrCode() {
        this.delegate = new QrCode();
    }

    /**
     * Generates a URL for OTP (One-Time Password) based authenticator apps.
     * This URL can be used by apps like Google Authenticator for generating time-based one-time passwords.
     *
     * @param secret The shared secret used for generating OTPs.
     * @param account The account associated with the OTP.
     * @param issuer The name of the issuer (usually the service name).
     * @return A URL in the format of "otpauth://totp/{issuer}:{account}?secret={secret}&issuer={issuer}"
     * @throws HapQrCodeException If any of the input parameters are null or empty.
     */
    @Override
    public String getAuthenticatorUrl(String secret, String account, String issuer) {
        return delegate.getAuthenticatorUrl(secret, account, issuer);
    }

    /**
     * Generates a QR code image from the provided parameters. The QR code can contain a URL or any
     * string data, and it can be customized with parameters like width, height, color, transparency, etc.
     * Optionally, a logo can be added to the center of the QR code.
     *
     * @param param A `QrCodeParam` object containing the customization options and URL for the QR code.
     * @return A Base64-encoded string representing the generated QR code image in PNG format.
     * @throws HapQrCodeException If any error occurs during QR code generation or if the parameters are invalid.
     */
    @Override
    public String generate(QrCodeRequest param) {
        return delegate.generate(param);
    }
}