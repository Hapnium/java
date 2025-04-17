package com.hapnium.core;

import com.hapnium.core.models.NotificationMessage;

/**
 * <h2>NotificationService</h2>
 * Defines a contract for sending notifications to individual devices or users.
 * <p>
 * Implementations of this interface should provide the logic necessary to send
 * messages (e.g., push notifications, in-app alerts, etc.) using an underlying
 * messaging platform or transport layer.
 * </p>
 */
interface NotificationService {
    /**
     * Sends a notification message to a target recipient.
     * <p>
     * This method accepts a {@link NotificationMessage} object that encapsulates
     * all required information such as the recipient token, message body, title,
     * and any optional metadata (e.g., priority or time-to-live settings).
     * </p>
     * <p>
     * The payload is generic to allow flexibility in the message body depending
     * on the specific application or platform requirements.
     * </p>
     *
     * @param request the {@link NotificationMessage} containing message details and metadata;
     *                must not be {@code null}.
     * @param <T>     the type of the payload carried in the message.
     *
     * @return a {@link String} representing the message ID or status returned by the messaging provider.
     *
     * @throws IllegalArgumentException if the message is malformed or required fields are missing.
     * @throws com.hapnium.core.exceptions.HapNotificationException if the message could not be sent
     *         due to transport errors, invalid credentials, or other internal failures.
     */
    <T> String send(NotificationMessage<T> request);
}