package com.hapnium.core;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * The TimeUtil class provides utility methods for working with dates and times.
 */
public class TimeUtil {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");

    /**
     * Formats the time difference in minutes, hours, or days before or after the current time.
     * @param diff The time difference in minutes.
     * @return The formatted time difference string.
     */
    public static String formatTimeDifference(long diff) {
        long absDiff = Math.abs(diff);

        if (diff < 0) {
            if (absDiff < 60) {
                return absDiff + " minutes before";
            } else {
                long hours = absDiff / 60;
                long minutes = absDiff % 60;
                if (minutes == 0) {
                    return hours + " hour(s) before";
                } else {
                    return hours + " hour(s) and " + minutes + " minutes before";
                }
            }
        } else {
            if (absDiff < 60) {
                return absDiff + " minutes after";
            } else {
                long hours = absDiff / 60;
                long minutes = absDiff % 60;
                if (minutes == 0) {
                    return hours + " hour(s) after";
                } else {
                    return hours + " hour(s) and " + minutes + " minutes after";
                }
            }
        }
    }

    /**
     * Formats the last seen time based on the provided last seen date and time.
     * @param lastSignedIn The date and time when the user was last seen.
     * @return The formatted last signed in time.
     */
    public static String formatLastSignedIn(ZonedDateTime lastSignedIn, String timezone, boolean needText) {
        if (lastSignedIn == null) {
            return "Never";
        }

        return formatDuration(Duration.between(lastSignedIn, toUserTimeZone(now(), timezone)), needText);
    }

    private static String formatDuration(Duration duration, boolean needText) {
        if (duration.toDays() > 0) {
            if (duration.toDays() == 1) {
                return needText ? "Last Signed In: 1 day ago" : "1 day ago";
            } else {
                return needText ? "Last Signed In: " + duration.toDays() + " days ago" : duration.toDays() + " days ago";
            }
        } else if (duration.toHours() > 0) {
            if (duration.toHours() == 1) {
                return needText ? "Last Signed In: 1 hour ago" : "1 hour ago";
            } else {
                return needText ? "Last Signed In: " + duration.toHours() + " hours ago" : duration.toHours() + " hours ago";
            }
        } else if (duration.toMinutes() > 0) {
            if (duration.toMinutes() == 1) {
                return needText ? "Last Signed In: 1 minute ago" : "1 minute ago";
            } else {
                return needText ? "Last Signed In: " + duration.toMinutes() + " minutes ago" : duration.toMinutes() + " minutes ago";
            }
        } else {
            return "Online";
        }
    }

    /**
     * Formats the future time based on the provided date and time.
     * @param dateTime The future date and time.
     * @return The formatted future time.
     */
    public static String formatFutureTime(ZonedDateTime dateTime, String timezone) {
        return formatDurationFuture(Duration.between(toUserTimeZone(now(), timezone), dateTime));
    }

    private static String formatDurationFuture(Duration duration) {
        long seconds = duration.toSeconds() % 60;
        long minutes = duration.toMinutes() % 60;

        StringBuilder result = new StringBuilder();
        if (minutes > 0) {
            result.append(minutes).append(" ")
                    .append(minutes == 1 ? "minute" : "minutes")
                    .append(" ")
                    .append(seconds).append(" ")
                    .append(seconds == 1 ? "second" : "seconds");
        } else {
            result.append(seconds).append(" ")
                    .append(seconds == 1 ? "second" : "seconds");
        }

        return result.toString();
    }

    /**
     * Formats the time based on the provided date and time.
     *
     * @param timezone The timezone of the user
     * @param time The time to format {@link LocalTime}
     * @param date The date to format {@link LocalDate}.
     *
     * @return The formatted time.
     */
    public static String formatTime(LocalDate date, LocalTime time, String timezone) {
        ZoneId zoneId = zoneId(timezone);
        ZonedDateTime dateTime = ZonedDateTime.of(date, time, zoneId);

        return formatTime(dateTime, timezone);
    }

    /**
     * Formats the time based on the provided date and time.
     * @param dateTime The date and time to format.
     * @return The formatted time.
     */
    public static String formatTime(ZonedDateTime dateTime, String timezone) {
        return toUserTimeZone(dateTime, timezone).format(DateTimeFormatter.ofPattern("h:mma"));
    }

    public static String formatDate(String timezone, LocalDate date) {
        LocalDate now = LocalDate.now(zoneId(timezone));

        if (date.equals(now)) {
            return "Today, " + date.format(DateTimeFormatter.ofPattern("d MMMM, yyyy"));
        } else if (date.equals(now.minusDays(1))) {
            return "Yesterday, " + date.format(DateTimeFormatter.ofPattern("d MMMM, yyyy"));
        } else {
            return date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM, yyyy"));
        }
    }

    /**
     * Formats the day based on the provided date and time.
     * @param date The date and time to format.
     * @return The formatted day.
     */
    public static String formatDay(LocalDateTime date, String timezone) {
        if (date != null) {
            return getDateString(timezone, now(zoneId(timezone)), ZonedDateTime.of(date, zoneId(timezone)));
        } else {
            return "";
        }
    }

    private static String getDateString(String timezone, ZonedDateTime currentDateTime, ZonedDateTime dateTime) {
        if (dateTime.toLocalDate().equals(currentDateTime.toLocalDate())) {
            return "Today, %s".formatted(formatTime(dateTime, timezone));
        } else if (dateTime.toLocalDate().equals(currentDateTime.minusDays(1).toLocalDate())) {
            return "Yesterday, %s".formatted(formatTime(dateTime, timezone));
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));
        }
    }

    /**
     * Formats the day based on the provided date and time.
     * @param dateTime The date and time to format.
     * @return The formatted day.
     */
    public static String formatDay(ZonedDateTime dateTime, String timezone) {
        if (dateTime != null) {
            return getDateString(timezone, now(zoneId(timezone)), dateTime);
        } else {
            return "";
        }
    }

    public static String formatChatLabel(LocalDateTime time, String timezone) {
        return getLabel(ZonedDateTime.of(time, zoneId(timezone)), timezone);
    }

    public static String getLabel(ZonedDateTime date, String timezone) {
        ZonedDateTime today = now(zoneId(timezone)).truncatedTo(ChronoUnit.DAYS);

        if (date.truncatedTo(ChronoUnit.DAYS).isEqual(today)) {
            return "Today";
        } else if (date.truncatedTo(ChronoUnit.DAYS).isEqual(today.minusDays(1))) {
            return "Yesterday";
        } else if (date.isAfter(today.minusDays(7))) {
            return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        } else {
            return date.format(DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy"));
        }
    }

    /**
     * Checks if the OTP (One-Time Password) has expired based on the provided time and expiration time.
     * @param time The time the OTP was generated.
     * @param expirationTime The expiration time for the OTP in minutes.
     * @return True if the OTP has expired; otherwise, false.
     */
    public static boolean isOtpExpired(ZonedDateTime time, String timezone, int expirationTime) {
        if (time != null) {
            return now(zoneId(timezone)).isAfter(toUserTimeZone(time, timezone).plusMinutes(expirationTime));
        } else {
            return true;
        }
    }


    public static String log() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy (EEEE) - h:mma", Locale.ENGLISH);
        return LocalDateTime.now().format(formatter);
    }

    public static ZoneId defaultZone() {
        return ZoneId.of("Africa/Lagos");
    }

    public static ZoneId zoneId(String timezone) {
        if(timezone != null && !timezone.isEmpty()) {
            return ZoneId.of(timezone);
        } else{
            return defaultZone();
        }
    }

    public static ZonedDateTime toZonedDate(LocalDateTime date, String timezone) {
        return ZonedDateTime.of(date, zoneId(timezone));
    }

    public static ZonedDateTime toUserTimeZone(ZonedDateTime time, String timeZone) {
        return time.withZoneSameInstant(zoneId(timeZone));
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    public static ZonedDateTime now(String timezone) {
        return ZonedDateTime.now(zoneId(timezone));
    }

    public static ZonedDateTime now(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    public static LocalTime toTime(String timeString) {
        return LocalTime.parse(timeString.toUpperCase(), TIME_FORMATTER);
    }

    public static String toString(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }
}