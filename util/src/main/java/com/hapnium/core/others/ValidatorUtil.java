package com.hapnium.core.others;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    /**
     * Validates a password based on a regular expression pattern.
     * @param password The password to validate.
     * @return True if the password is valid, otherwise false.
     */
    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        );
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}