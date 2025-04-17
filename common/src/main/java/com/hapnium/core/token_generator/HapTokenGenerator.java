package com.hapnium.core.token_generator;

import com.hapnium.core.token_generator.models.TokenParam;

/**
 * <h1>HapTokenGenerator</h1>
 * {@code HapTokenGenerator} serves as a higher-level abstraction for generating tokens and OTPs.
 * It delegates all generation logic to the {@link TokenGenerator} class but adheres to the {@link TokenGeneratorService}
 * interface for consistent behavior across implementations.
 * <p>
 * This abstraction enables easier unit testing, dependency injection, and future enhancements or customizations.
 * </p>
 */
public class HapTokenGenerator implements TokenGeneratorService {
    private final TokenGeneratorService delegate;

    /**
     * Constructs a {@code HapTokenGenerator} using default parameters.
     */
    public HapTokenGenerator() {
        this.delegate = new TokenGenerator();
    }

    /**
     * Constructs a {@code HapTokenGenerator} using the provided custom parameters.
     *
     * @param param A {@link TokenParam} object defining OTP and token generation settings.
     */
    public HapTokenGenerator(TokenParam param) {
        this.delegate = new TokenGenerator(param);
    }


    @Override
    public String generateOtp() {
        return delegate.generateOtp();
    }

    @Override
    public String generateWithOtp(Integer length) {
        return delegate.generateWithOtp(length);
    }

    @Override
    public String generate() {
        return delegate.generate();
    }

    @Override
    public String generate(Integer length) {
        return delegate.generate(length);
    }

    @Override
    public String generate(String characters, Integer length) {
        return delegate.generate(characters, length);
    }
}