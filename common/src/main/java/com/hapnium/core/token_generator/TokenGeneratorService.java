package com.hapnium.core.token_generator;

/**
 * <h1>TokenService</h1>
 * <p></p>
 * This interface provides methods for generating tokens and One-Time Passwords (OTPs) with different configurations.
 * It offers flexibility to generate:
 * <ul>
 *   <li>Standard OTPs.</li>
 *   <li>Codes using otp characters.</li>
 *   <li>General tokens with customizable length and characters.</li>
 * </ul>
 */
interface TokenGeneratorService {
    /**
     * Generates a one-time password (OTP) using the default OTP characters and length.
     * <p>
     * This method generates an OTP using predefined settings (typically numeric and a fixed length,
     * such as 6 digits), and it can be used for applications requiring simple OTPs.
     * </p>
     *
     * @return A string representing the generated OTP.
     */
    String generateOtp();

    /**
     * Generates a code using the default OTP characters and a specified length.
     * <p>
     * This method allows you to generate an OTP-like code for other uses, with a defined length.
     * For example, generating multi-factor recovery codes.
     * </p>
     *
     * @param length The length of the code to be generated.
     * @return A string representing the generated code.
     */
    String generateWithOtp(Integer length);

    /**
     * Generates a general token_generator using a default set of characters and a default length.
     * <p>
     * This method generates a token_generator with default settings, typically alphanumeric and a fixed length.
     * This method is used when a standard token_generator is required.
     * </p>
     *
     * @return A string representing the generated token_generator.
     */
    String generate();

    /**
     * Generates a general token_generator using a default set of characters and a specified length.
     * <p>
     * This method allows customization of the token_generator's length. It uses the default set of alphanumeric characters
     * to generate the token_generator.
     * </p>
     *
     * @param length The length of the token_generator to be generated.
     * @return A string representing the generated token_generator.
     */
    String generate(Integer length);

    /**
     * Generates a general token_generator using a custom set of characters and a specified length.
     * <p>
     * This method allows you to generate a token_generator with custom characters (e.g., uppercase letters,
     * lowercase letters, numbers, or special characters) and a specified length.
     * </p>
     *
     * @param characters A string representing the characters to be used in the token_generator.
     * @param length The length of the token_generator to be generated.
     * @return A string representing the generated token_generator.
     */
    String generate(String characters, Integer length);
}