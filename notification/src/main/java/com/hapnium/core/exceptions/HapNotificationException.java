package com.hapnium.core.exceptions;

public class HapNotificationException extends RuntimeException {
    public HapNotificationException(String message) {
        super(message);
    }

    public HapNotificationException(String message, Throwable cause) {
      super(message, cause);
    }
}
