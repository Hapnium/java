package com.hapnium.core.models;

import lombok.Data;

/**
 * <h2>NotificationMessage</h2>
 * Represents the structure of a notification message sent to a client device.
 * <p>
 * This message consists of a device token, a notification category type (nct),
 * and a generic {@link Details} object containing the message content and any
 * custom payload data.
 * </p>
 *
 * @param <T> the type of the custom data payload.
 */
@Data
public class NotificationMessage<T> {
    /**
     * The target device token to which the notification should be delivered.
     */
    private String token;

    /**
     * Notification category/type used for grouping or routing messages (optional).
     */
    private String category;

    /**
     * The details of the notification including title, body, image, and custom payload.
     */
    private Details<T> data;

    /**
     * <h3>Details</h3>
     * Represents the inner content of a notification message.
     *
     * @param <T> the type of the optional custom data payload.
     */
    @Data
    public static class Details<T> {
        /**
         * The title of the notification displayed to the user.
         */
        private String title;

        /**
         * The body content or main message of the notification.
         */
        private String body;

        /**
         * An optional URL to an image to be displayed in the notification.
         */
        private String image;

        /**
         * The custom data payload to include with the notification.
         */
        private T data;
    }
}