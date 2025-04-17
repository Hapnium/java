package com.hapnium.core.mfa;

/**
 * <h1>HapMFA</h1>
 * The {@code HapMFA} class serves as a public-facing implementation of {@link MFAService}
 * and delegates all method calls to the internal {@link MFA} class.
 * <p>
 * It provides a simple interface to:
 * <ul>
 *     <li>Generate a shared TOTP secret</li>
 *     <li>Generate TOTP codes</li>
 *     <li>Validate TOTP codes</li>
 * </ul>
 * This abstraction allows easy future replacement or extension of MFA logic.
 */
public class HapMFA implements MFAService {
    private final MFAService delegate;

    /**
     * Constructs a new {@code HapMFA} instance with default internal implementation.
     */
    public HapMFA() {
        this.delegate = new MFA();
    }

    @Override
    public String generateSecret(Boolean readable) {
        return delegate.generateSecret(readable);
    }

    @Override
    public String getCode(String secret) {
        return delegate.getCode(secret);
    }

    @Override
    public boolean isValid(String code, String secret) {
        return delegate.isValid(code, secret);
    }
}