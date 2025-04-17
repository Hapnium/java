package com.hapnium.core;

import com.hapnium.core.models.NotificationMessage;

/**
 * <h2>HapNotification</h2>
 * Public-facing implementation of {@link NotificationService} for sending notifications.
 * <p>
 * This acts as a simplified and flexible wrapper over the core {@link Notification} class, making it easier to instantiate
 * with logging options and abstracting direct Firebase usage behind the scenes.
 * </p>
 */
public class HapNotification implements NotificationService {
    private final NotificationService delegate;

    /**
     * Constructs a new {@code HapNotification} with default logging enabled.
     *
     * @param key Firebase credentials as a JSON string or URL.
     */
    public HapNotification(String key) {
        this.delegate = new Notification(key);
    }

    /**
     * Factory method for creating a {@code HapNotification} with optional logging control.
     *
     * @param key      Firebase credentials as a JSON string or URL.
     * @param showLog  whether to enable internal logging of notification requests/responses.
     * @return a new instance of {@code HapNotification}.
     */
    public static HapNotification create(String key, Boolean showLog) {
        return new HapNotification(new Notification(key, showLog));
    }

    /**
     * Private constructor used internally for delegation.
     *
     * @param notification internal {@link Notification} instance.
     */
    private HapNotification(Notification notification) {
        this.delegate = notification;
    }

    @Override
    public <T> String send(NotificationMessage<T> request) {
        return delegate.send(request);
    }
}