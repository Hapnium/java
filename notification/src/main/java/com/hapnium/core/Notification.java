package com.hapnium.core;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.hapnium.core.exceptions.HapNotificationException;
import com.hapnium.core.models.NotificationMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Notification</h2>
 * Implementation of {@link NotificationService} for sending push notifications using Firebase Cloud Messaging (FCM).
 * <p>
 * Initializes Firebase with provided credentials and sends structured messages to device tokens.
 * Logging can be enabled for debug and trace purposes.
 * </p>
 */
@Slf4j
@Getter
class Notification implements NotificationService {
    private final FirebaseApp firebase;
    private final Boolean logInformation;

    /**
     * Constructs a {@code Notification} service instance with Firebase initialized using the given key.
     *
     * @param key             Firebase service account key as a JSON string or URL.
     * @param logInformation  If true, logs request/response details to the console.
     */
    Notification(String key, Boolean logInformation) {
        FirebaseSetup setup = FirebaseSetup.getInstance();
        this.firebase = FirebaseApp.initializeApp(setup.getOptions(key));
        this.logInformation = logInformation;

        if(logInformation) {
            logInitialization();
        }
    }

    /**
     * Constructs a {@code Notification} service with logging enabled by default.
     *
     * @param key Firebase service account key as a JSON string or URL.
     */
    Notification(String key) {
        FirebaseSetup setup = FirebaseSetup.getInstance();
        this.firebase = FirebaseApp.initializeApp(setup.getOptions(key));
        this.logInformation = true;

        logInitialization();
    }

    private void logInitialization() {
        log.info("NOTIFICATION SDK INITIALIZER::: Firebase Initialized for {}", this.firebase.getName());
        log.info("NOTIFICATION SDK INITIALIZER::: Firebase Initialized with {}", this.firebase.getOptions());
    }

    @Override
    public <T> String send(NotificationMessage<T> request) {
        Message message = Message.builder()
                .setToken(request.getToken())
                .putAllData(toMap(request.getData(), request.getCategory()))
                .build();

        if(logInformation) {
            System.out.printf("%s::: %s", "NOTIFICATION SDK REQUEST", request.getToken());
        }

        try {
            String response = FirebaseMessaging.getInstance().send(message);

            if(logInformation) {
                System.out.printf("%s::: %s", "NOTIFICATION SDK RESPONSE", response);
            }

            return response;
        } catch (FirebaseMessagingException e) {
            throw new HapNotificationException(String.format("%s::: %s", "NOTIFICATION SDK EXCEPTION", e));
        }
    }

    /**
     * Converts the {@link NotificationMessage.Details} and category into a string-based map
     * suitable for use with Firebase's {@link Message} data payload.
     *
     * @param data     structured notification content and metadata.
     * @param category category/type of the notification (used for filtering or display).
     * @param <T>      the type of custom payload inside {@code data}.
     * @return a {@link Map} of key-value pairs formatted as strings.
     * @throws HapNotificationException if payload data serialization fails.
     */
    private <T> Map<String, String> toMap(NotificationMessage.Details<T> data, String category) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        Map<String, String> map = new HashMap<>();

        if (data != null) {
            map.put("title", data.getTitle());
            map.put("body", data.getBody());
            map.put("category", category);

            if (data.getImage() != null) {
                map.put("image", data.getImage());
            }

            try {
                map.put("data", objectMapper.writeValueAsString(data.getData()));
            } catch (Exception e) {
                throw new HapNotificationException("Failed to serialize 'data' field in notification message: " + e.getMessage(), e);
            }
        }

        return map;
    }
}