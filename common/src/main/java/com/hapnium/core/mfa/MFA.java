package com.hapnium.core.mfa;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

/**
 * <h1>MFA</h1>
 * The {@code MFA} class provides core functionalities for multi-factor authentication (MFA)
 * using time-based one-time passwords (TOTP). It handles:
 * <ul>
 *     <li>Secure generation of Base32-encoded secrets</li>
 *     <li>Generating time-based codes based on a shared secret</li>
 *     <li>Validating TOTP codes against the current time</li>
 * </ul>
 * This class is intended to be used internally and accessed via the {@link HapMFA} wrapper.
 */
class MFA implements MFAService {
    private final TOTP totp = new TOTP();

    @Override
    public String generateSecret(@NotNull Boolean readable) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        String secret = new Base32().encodeToString(bytes);

        if(readable) {
            // make the secret key more human-readable by lower-casing and
            // inserting spaces between each group of 4 characters
            return secret.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
        } else {
            return secret;
        }
    }

    @Override
    public String getCode(@NotNull String secret) {
        String hexKey = Hex.encodeHexString(new Base32().decode(secret.replace(" ", "").toUpperCase()));
        String hexTime = Long.toHexString((System.currentTimeMillis() / 1000) / 30);

        return totp.generate(hexKey, hexTime, "6");
    }

    @Override
    public boolean isValid(@NotNull String code, String secret) {
        return code.equals(getCode(secret));
    }
}