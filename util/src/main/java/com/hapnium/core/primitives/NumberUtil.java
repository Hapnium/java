package com.hapnium.core.primitives;

public class NumberUtil {
    /**
     * Convert long numbers to human-readable strings.
     * Example: 3300 -> "3.3k", 2300000 -> "2.3M", 1200000000 -> "1.2B".
     * @param number The number to convert.
     * @return A human-readable string representation of the number.
     */
    public static String format(long number) {
        if (number < 1000) return String.valueOf(number);
        if (number < 1_000_000) return String.format("%.1fk", number / 1000.0);
        if (number < 1_000_000_000) return String.format("%.1fM", number / 1_000_000.0);

        return String.format("%.1fB", number / 1_000_000_000.0);
    }
}