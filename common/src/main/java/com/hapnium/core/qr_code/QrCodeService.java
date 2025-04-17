package com.hapnium.core.qr_code;

import com.hapnium.core.exception.HapQrCodeException;
import com.hapnium.core.qr_code.models.QrCodeRequest;

/**
 * <h1>QrCodeService Interface</h1>
 * This interface defines the contract for services that handle QR code generation,
 * specifically for use in scenarios like multi-factor authentication (MFA).
 * Implementations of this interface should provide functionality to generate a URL for an
 * authenticator app as well as generate the QR code itself based on the given parameters.
 */
interface QrCodeService {
    /**
     * Constructs a URI for generating a QR code to assist the user in setting up their MFA authenticator app.
     * <p>
     * The URI is formatted according to the Key URI format as specified by Google Authenticator,
     * and it includes the secret key, account name, and issuer information.
     * </p>
     *
     * <p>
     * This URL is commonly used by authenticator apps (e.g., Google Authenticator or Authy)
     * to generate time-based one-time passwords (TOTP) for the user. The application can scan
     * this URL as a QR code and use it to generate OTPs.
     * </p>
     *
     * @param secret  The Base32 encoded secret key (may contain optional whitespace) used for OTP generation.
     * @param account The user's account name (e.g., email address, username) associated with the MFA setup.
     * @param issuer  The organization managing the account, typically the name of the application (e.g., "MyApp").
     *
     * @return A {@link String} representing the URI for the QR code, which can be converted into a QR code image.
     *
     * @see <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format">Key URI Format</a>
     */
    String getAuthenticatorUrl(String secret, String account, String issuer);

    /**
     * Generates a QR code image based on the provided parameters.
     * <p>
     * This method takes a {@link QrCodeRequest} object which contains the URL or data to encode in the QR code,
     * along with various customization options such as width, height, colors, and whether to include a logo.
     * </p>
     *
     * @param param A {@link QrCodeRequest} object that encapsulates the parameters for generating the QR code.
     * @return A {@link String} representing the QR code image as a Base64-encoded PNG.
     *
     * @throws HapQrCodeException If there is an issue with the generation process (e.g., invalid parameters).
     */
    String generate(QrCodeRequest param);
}