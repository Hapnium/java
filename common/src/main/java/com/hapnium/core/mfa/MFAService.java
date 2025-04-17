package com.hapnium.core.mfa;

/**
 * Service interface for managing Multi-Factor Authentication (MFA) using Time-Based One-Time Passwords (TOTP).
 * <p>
 * This interface provides methods for generating secret keys, creating QR codes for MFA setup,
 * generating TOTP codes, and validating the TOTP codes against user accounts. Implementations
 * of this interface should handle the necessary logic for TOTP generation and verification.
 * </p>
 */
interface MFAService {

    /**
     * Generates a secret key for the user's MFA Authentication setup.
     * <p>
     * This secret key is unique to the user and is used in generating time-based one-time passwords (TOTP).
     * </p>
     *
     * @return A {@link String} representing the generated secret key in Base32 format.
     */
    String generateSecret(Boolean readable);

    /**
     * Generates a TOTP code for validating the secret key against the authenticator app.
     * <p>
     * This code is time-sensitive and changes based on the current time and the user's secret key.
     * </p>
     *
     * @param secret The generated secret key unique to the user.
     *
     * @return A {@link String} representing the generated TOTP code.
     */
    String getCode(String secret);

    /**
     * Validates the provided TOTP code against the user's secret key.
     * <p>
     * This method checks whether the code entered by the user matches the expected code generated
     * using the secret key according to the MFA implementation logic.
     * </p>
     *
     * @param code      The code obtained from the authenticator app.
     * @param secret The secret key of the user for MFA.
     *
     * @return {@code true} if the code is valid and matches the expected value; {@code false} otherwise.
     */
    boolean isValid(String code, String secret);
}