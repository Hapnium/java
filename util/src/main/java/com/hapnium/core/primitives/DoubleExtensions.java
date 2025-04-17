package com.hapnium.core.primitives;

import java.time.Duration;
import java.util.List;

public class DoubleExtensions {
    // Case equality check
    public static boolean equals(double a, double b) {
        return Double.compare(a, b) == 0;
    }

    // Checks if double equals any item in the list
    public static boolean equalsAny(double a, List<Double> values) {
        return values.stream().anyMatch(v -> equals(a, v));
    }

    // Checks if double equals all items in the list
    public static boolean equalsAll(double a, List<Double> values) {
        return values.stream().allMatch(v -> equals(a, v));
    }

    // Case inequality check
    public static boolean notEquals(double a, double b) {
        return !equals(a, b);
    }

    // Checks if double does not equal any item
    public static boolean notEqualsAny(double a, List<Double> values) {
        return values.stream().noneMatch(v -> equals(a, v));
    }

    // Checks if double does not equal all items
    public static boolean notEqualsAll(double a, List<Double> values) {
        return values.stream().allMatch(v -> equals(a, v));
    }

    // Returns the length of this double value (without the decimal point)
    public static int length(double a) {
        return String.valueOf(a).replace(".", "").length();
    }

    // isLessThan
    public static boolean isLessThan(double a, double b) {
        return a < b;
    }

    // isLessThanOrEqualTo
    public static boolean isLessThanOrEqualTo(double a, double b) {
        return a < b || equals(a, b);
    }

    // isGreaterThan
    public static boolean isGreaterThan(double a, double b) {
        return a > b;
    }

    // isGreaterThanOrEqualTo
    public static boolean isGreaterThanOrEqualTo(double a, double b) {
        return a > b || equals(a, b);
    }

    // Checks if length of double is greater than
    public static boolean isLengthGreaterThan(double a, int max) {
        return length(a) > max;
    }

    // Checks if length of double is greater than or equal to
    public static boolean isLengthGreaterThanOrEqualTo(double a, int max) {
        return length(a) >= max;
    }

    // Checks if length of double is less than
    public static boolean isLengthLessThan(double a, int max) {
        return length(a) < max;
    }

    // Checks if length of double is less than or equal to
    public static boolean isLengthLessThanOrEqualTo(double a, int max) {
        return length(a) <= max;
    }

    // Checks if length of double is equal to
    public static boolean isLengthEqualTo(double a, int other) {
        return length(a) == other;
    }

    // Checks if length of double is between min and max
    public static boolean isLengthBetween(double a, int min, int max) {
        return isLengthGreaterThanOrEqualTo(a, min) && isLengthLessThanOrEqualTo(a, max);
    }

    // Rounds the double value to the specified number of decimal places
    public static double toPrecision(double a, int fractionDigits) {
        double mod = Math.pow(10, fractionDigits);
        return Math.round(a * mod) / mod;
    }

    // Divide by value
    public static double divideBy(double a, double value) {
        return a / value;
    }

    // Multiply by value
    public static double multiplyBy(double a, double value) {
        return a * value;
    }

    // Add value
    public static double plus(double a, double value) {
        return a + value;
    }

    // Subtract value
    public static double minus(double a, double value) {
        return a - value;
    }

    // Format to decimal places
    public static String toDp(double a, int decimalPlaces) {
        return String.format("%." + decimalPlaces + "f", a);
    }

    public static String toDp(double a) {
        return toDp(a, 2); // default 2 decimal places
    }

    // Convert double to Duration in milliseconds
    public static Duration milliseconds(double a) {
        return Duration.ofMillis(Math.round(a * 1000));
    }

    // Alias for milliseconds
    public static Duration ms(double a) {
        return milliseconds(a);
    }

    // Convert double to Duration in seconds
    public static Duration seconds(double a) {
        return Duration.ofMillis(Math.round(a * 1000));
    }

    // Convert double to Duration in minutes
    public static Duration minutes(double a) {
        return Duration.ofSeconds(Math.round(a * 60));
    }

    // Convert double to Duration in hours
    public static Duration hours(double a) {
        return Duration.ofMinutes(Math.round(a * 60));
    }

    // Convert double to Duration in days
    public static Duration days(double a) {
        return Duration.ofHours(Math.round(a * 24));
    }

    // Distance formatting
    public static String distance(double a) {
        if (isGreaterThanOrEqualTo(a, 1000)) {
            double kilometers = a / 1000;
            return toDp(kilometers) + " km";
        } else {
            return toDp(a) + " m";
        }
    }

    // Remainder
    public static double remainder(double a, double value) {
        return a % value;
    }

    // Integer quotient
    public static int iq(double a, double value) {
        return (int) (a / value);
    }

    // Negate
    public static double negated(double a) {
        return -a;
    }

    // Format media duration
    public static String mediaDuration(double a, boolean addSpacing) {
        int totalSeconds = (int) a;

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        String sep = addSpacing ? " : " : ":";

        if (hours > 0) {
            return String.format("%02d%s%02d%s%02d", hours, sep, minutes, sep, seconds);
        } else {
            return String.format("%02d%s%02d", minutes, sep, seconds);
        }
    }

    public static String mediaDuration(double a) {
        return mediaDuration(a, true);
    }
}