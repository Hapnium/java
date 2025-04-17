package com.hapnium.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The MoneyUtil class provides utility methods for formatting currency, generating options based on an amount,
 * calculating the percentage spent, and generating shared information about money distribution.
 */
public class MoneyUtil {
    /**
     * Formats the given amount to Nigerian Naira currency format.
     * @param amount The amount to format.
     * @return The formatted amount in Nigerian Naira currency.
     */
    public static String formatToNaira(BigDecimal amount) {
        // Format the BigDecimal as a currency string
        if(amount == null) {
            amount = BigDecimal.ZERO;
        }

        String result = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-NG")).format(amount);
        // Remove any non-breaking space characters from the formatted string
        result = result.replace("\u00A0", "");
        return result.replace("\u00A0", "");
    }

    /**
     * Formats the given amount to US Dollar currency format.
     * @param amount The amount to format.
     * @return The formatted amount in US Dollar currency.
     */
    public static String formatToDollar(BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        String result = NumberFormat.getCurrencyInstance(Locale.US).format(amount);
        return result.replace("\u00A0", "");
    }

    /**
     * Formats the given amount to readable currency format.
     * @param amount The amount to format.
     * @return The formatted amount in readable currency.
     */
    public static String format(BigDecimal amount, String currency) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        if(currency == null) {
            currency = "CNY";
        }

        if(currency.equalsIgnoreCase("ngn") || currency.equalsIgnoreCase("ng")) {
            return formatToNaira(amount);
        } else {
            return formatToDollar(amount);
        }
    }

    public static BigDecimal parseFromNaira(String nairaAmount) {
        if(nairaAmount != null && !nairaAmount.isEmpty()) {
            // Remove the naira symbol and any commas from the string
            String cleanAmount = nairaAmount.replaceAll("[^\\d.]", "");
            // Parse the cleaned string as a BigDecimal
            return new BigDecimal(cleanAmount);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Generates a list of options based on the given amount with dynamic intervals.
     * @param amount The base amount to generate options from.
     * @return A list of formatted options.
     */
    public static List<String> generateOptionsFromAmount(BigDecimal amount) {
        List<String> options = new ArrayList<>();
        BigDecimal minAmount = amount
                .multiply(BigDecimal.valueOf(80))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
        BigDecimal maxAmount = amount
                .multiply(BigDecimal.valueOf(120))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.UP);
        BigDecimal dynamicInterval = amount.divide(BigDecimal.valueOf(24), 0, RoundingMode.DOWN);

        for (BigDecimal i = minAmount; i.compareTo(maxAmount) <= 0; i = i.add(dynamicInterval)) {
            options.add(formatToNaira(i));
        }
        return options;
    }
}
