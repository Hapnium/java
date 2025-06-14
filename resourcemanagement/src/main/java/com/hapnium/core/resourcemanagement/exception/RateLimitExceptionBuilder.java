package com.hapnium.core.resourcemanagement.exception;

import com.hapnium.core.resourcemanagement.rate_limit.models.RateLimitResult;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RateLimitExceptionBuilder {
    public static final String RATE_LIMIT_HEADER = "X-RateLimit-Limit";
    public static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";
    public static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";
    public static final String RATE_LIMIT_RETRY_AFTER_HEADER = "Retry-After";
    public static final String RATE_LIMIT_STRATEGY_HEADER = "X-RateLimit-Strategy";

    public static HttpHeaders buildRateLimitHeaders(RateLimitResult res) {
        HttpHeaders headers = new HttpHeaders();

        if (res != null) {
            // Standard rate limit headers
            headers.add(RATE_LIMIT_HEADER, String.valueOf(res.getLimit()));
            headers.add(RATE_LIMIT_REMAINING_HEADER, String.valueOf(res.getRemainingRequests()));

            if (res.getResetTime() != null) {
                // Unix timestamp for reset time
                headers.add(RATE_LIMIT_RESET_HEADER, String.valueOf(res.getResetTime().getEpochSecond()));
            }

            if (res.getTimeUntilReset() != null) {
                // Retry after in seconds
                headers.add(RATE_LIMIT_RETRY_AFTER_HEADER, String.valueOf(res.getTimeUntilReset().getSeconds()));
            }

            if (res.getStrategy() != null) {
                headers.add(RATE_LIMIT_STRATEGY_HEADER, res.getStrategy());
            }
        }

        return headers;
    }

    public static Map<String, Object> buildRateLimitData(RateLimitExceededException ex, RateLimitResult res) {
        Map<String, Object> data = new HashMap<>();

        // Basic exception information
        data.put("message", ex.getMessage());
        data.put("error_type", "RATE_LIMIT_EXCEEDED");
        data.put("error_code", "RL_001");
        data.put("timestamp", java.time.Instant.now().toString());

        if (res != null) {
            data.put("rate_limit", getRateLimitDetails(res));
            data.put("guidance", getUserGuidance(res));
        }

        return data;
    }

    private static @NonNull Map<String, Object> getRateLimitDetails(RateLimitResult res) {
        Map<String, Object> rateLimitDetails = new HashMap<>();
        rateLimitDetails.put("allowed", res.isAllowed());
        rateLimitDetails.put("limit", res.getLimit());
        rateLimitDetails.put("remaining", res.getRemainingRequests());
        rateLimitDetails.put("total_requests", res.getTotalRequests());
        rateLimitDetails.put("strategy", res.getStrategy());
        rateLimitDetails.put("window", res.getWindow());

        if (res.getResetTime() != null) {
            rateLimitDetails.put("reset_time", res.getResetTime().toString());
            rateLimitDetails.put("reset_time_unix", res.getResetTime().getEpochSecond());
            rateLimitDetails.put("reset_time_formatted", DateTimeFormatter.ISO_INSTANT.format(res.getResetTime()));
        }

        if (res.getTimeUntilReset() != null) {
            rateLimitDetails.put("retry_after_seconds", res.getTimeUntilReset().getSeconds());
            rateLimitDetails.put("retry_after_human", formatDuration(res.getTimeUntilReset()));
        }

        return rateLimitDetails;
    }

    private static @NonNull Map<String, Object> getUserGuidance(RateLimitResult res) {
        Map<String, Object> guidance = new HashMap<>();
        guidance.put("message", "You have exceeded the rate limit. Please slow down your requests.");

        if (res.getTimeUntilReset() != null) {
            guidance.put("retry_suggestion", String.format("Please wait %s before making another request.", formatDuration(res.getTimeUntilReset())));
        }

        guidance.put("current_usage", String.format("You have made %d out of %d allowed requests.", res.getTotalRequests(), res.getLimit()));

        if (res.getRemainingRequests() > 0) {
            guidance.put("remaining_quota", String.format("You have %d requests remaining in the current window.", res.getRemainingRequests()));
        }

        return guidance;
    }

    private static String formatDuration(java.time.Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            long hours = seconds / 3600;
            long remainingMinutes = (seconds % 3600) / 60;
            String result = hours + " hour" + (hours != 1 ? "s" : "");
            if (remainingMinutes > 0) {
                result += " and " + remainingMinutes + " minute" + (remainingMinutes != 1 ? "s" : "");
            }
            return result;
        }
    }
}