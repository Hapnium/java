package com.hapnium.core.token_generator.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>TokenParam</h1>
 * The `TokenParam` class is a data container that holds the configuration parameters for generating tokens
 * and one-time passwords (OTPs). It allows the user to define custom characters and lengths for both OTPs and tokens.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenParam {
    /**
     * A string representing the characters to be used for generating the OTP.
     * <p>
     * This field allows the user to specify a custom set of characters for OTP generation. If left null or empty,
     * default OTP characters will be used.
     * </p>
     */
    private String otpCharacters;

    /**
     * The length of the OTP to be generated.
     * <p>
     * This field allows the user to specify the length of the OTP. If not provided, the default OTP length will be used.
     * </p>
     */
    private Integer otpLength;

    /**
     * A string representing the characters to be used for generating the token_generator.
     * <p>
     * This field allows the user to specify a custom set of characters for token_generator generation. If left null or empty,
     * default token_generator characters will be used.
     * </p>
     */
    private String tokenCharacters;

    /**
     * The length of the token_generator to be generated.
     * <p>
     * This field allows the user to specify the length of the token_generator. If not provided, the default token_generator length will be used.
     * </p>
     */
    private Integer tokenLength;
}