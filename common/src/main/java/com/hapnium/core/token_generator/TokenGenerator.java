package com.hapnium.core.token_generator;

import com.hapnium.core.token_generator.models.TokenParam;

import java.security.SecureRandom;

/**
 * <h1>TokenGenerator</h1>
 * The {@code TokenGenerator} class is a low-level implementation responsible for generating tokens and OTPs
 * (One-Time Passwords) based on customizable parameters such as character sets and lengths.
 * <p>
 * It uses a secure random generator to ensure that the generated values are unpredictable and suitable
 * for security-sensitive contexts like authentication or session management.
 * </p>
 */
class TokenGenerator implements TokenGeneratorService {
    private final TokenParam param;
    private final SecureRandom random = new SecureRandom();

    /**
     * Constructs a {@code TokenGenerator} instance using default parameters.
     * Default values include:
     * <ul>
     *   <li>OTP characters: 0123456789</li>
     *   <li>OTP length: 6</li>
     *   <li>Token characters: 0-9, A-Z, a-z</li>
     *   <li>Token length: 64</li>
     * </ul>
     */
    TokenGenerator() {
        this.param = defaultParam();
    }

    /**
     * Constructs a {@code TokenGenerator} instance using the provided {@link TokenParam}.
     * Missing fields in the provided param will be replaced with their respective default values.
     *
     * @param param A {@link TokenParam} object defining custom token and OTP settings.
     */
    TokenGenerator(TokenParam param) {
        if(param != null) {
            if(param.getTokenCharacters() == null || param.getTokenCharacters().isEmpty()) {
                param.setTokenCharacters(defaultParam().getTokenCharacters());
            }

            if(param.getTokenLength() == null) {
                param.setTokenLength(defaultParam().getTokenLength());
            }

            if(param.getOtpCharacters() == null || param.getOtpCharacters().isEmpty()) {
                param.setOtpCharacters(defaultParam().getOtpCharacters());
            }

            if(param.getOtpLength() == null) {
                param.setOtpLength(defaultParam().getOtpLength());
            }

            this.param = param;
        } else {
            this.param = defaultParam();
        }
    }

    /**
     * Returns a {@link TokenParam} object with default values.
     *
     * @return Default {@link TokenParam}.
     */
    private TokenParam defaultParam() {
        TokenParam param = new TokenParam();
        param.setOtpCharacters("0123456789");
        param.setOtpLength(6);
        param.setTokenCharacters("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        param.setTokenLength(64);

        return param;
    }

    /**
     * Internal utility method to generate a random string based on the given length and characters.
     *
     * @param length     Desired length of the generated string.
     * @param characters Allowed characters to use in the string.
     * @return A {@link StringBuilder} containing the generated value.
     */
    private StringBuilder generate(int length, String characters) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }

        return token;
    }

    @Override
    public String generateOtp() {
        return generateWithOtp(param.getOtpLength());
    }

    @Override
    public String generateWithOtp(Integer length) {
        return generate(length, param.getOtpCharacters()).toString();
    }

    @Override
    public String generate() {
        return generate(param.getTokenLength());
    }

    @Override
    public String generate(Integer length) {
        return generate(length, param.getTokenCharacters()).toString();
    }

    @Override
    public String generate(String characters, Integer length) {
        return generate(length, characters.replaceAll(" ", "")).toString();
    }
}