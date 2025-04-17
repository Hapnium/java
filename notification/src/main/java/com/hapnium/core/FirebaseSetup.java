package com.hapnium.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import com.hapnium.core.exceptions.HapNotificationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/**
 * <h2>FirebaseSetup</h2>
 * Responsible for building and returning configured {@link FirebaseOptions} for Firebase initialization.
 * <p>
 * This utility supports reading service credentials either from a raw JSON string or a remote HTTP URL.
 * </p>
 * <p>
 * Handles credential loading, parsing, and automatic refreshing of Google access tokens.
 * </p>
 */
class FirebaseSetup {
    /**
     * Private constructor to enforce utility-style usage.
     */
    private FirebaseSetup() {}

    /**
     * Factory method to obtain an instance of {@code FirebaseSetup}.
     *
     * @return a new instance of {@code FirebaseSetup}.
     */
    public static FirebaseSetup getInstance() {
        return new FirebaseSetup();
    }

    /**
     * Creates a {@link FirebaseOptions} instance with credentials loaded from the provided key string.
     *
     * @param key Either a JSON string representing a service account or a URL pointing to one.
     * @return a configured {@link FirebaseOptions} object ready to initialize FirebaseApp.
     * @throws HapNotificationException if the credentials cannot be loaded or parsed.
     */
    public FirebaseOptions getOptions(String key) {
        return FirebaseOptions.builder().setCredentials(credentials(key)).build();
    }

    /**
     * Parses and loads {@link GoogleCredentials} from the provided key.
     * The key may be a raw JSON string or a URL pointing to a remote JSON source.
     *
     * @param key JSON credentials string or a URL to a credentials file.
     * @return an instance of {@link GoogleCredentials} with Firebase messaging scope.
     * @throws HapNotificationException on parsing or I/O failure.
     */
    private GoogleCredentials credentials(String key) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        if (key.startsWith("https")) {
            json = fetchSource(key, objectMapper);
        } else {
            HashMap account;
            try {
                account = objectMapper.readValue(key, HashMap.class);
            } catch (IOException e) {
                throw new HapNotificationException("Failed to parse local notification key JSON string: " + e.getMessage(), e);
            }

            try {
                json = objectMapper.writeValueAsString(account);
            } catch (IOException e) {
                throw new HapNotificationException("Failed to convert local account map to JSON string: " + e.getMessage(), e);
            }
        }

        GoogleCredentials credentials;
        try (InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
            credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        } catch (IOException e) {
            throw new HapNotificationException("Failed to load GoogleCredentials from input stream: " + e.getMessage(), e);
        }

        try {
            credentials.refreshIfExpired();
        } catch (IOException e) {
            throw new HapNotificationException("Failed to refresh GoogleCredentials: " + e.getMessage(), e);
        }

        return credentials;
    }

    /**
     * Fetches a remote JSON service account key from a URL and returns its JSON representation.
     *
     * @param key URL pointing to a remote JSON key.
     * @param objectMapper Jackson {@link ObjectMapper} for parsing the HTTP response.
     * @return a JSON string representing the Firebase service account credentials.
     * @throws HapNotificationException if HTTP request fails or response parsing fails.
     */
    private String fetchSource(String key, ObjectMapper objectMapper) {
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(key)).GET().build();

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new HapNotificationException("Failed to send HTTP request to fetch notification key: " + e.getMessage(), e);
            }
        }

        NotificationParam param;
        try {
            param = objectMapper.readValue(response.body(), NotificationParam.class);
        } catch (IOException e) {
            throw new HapNotificationException("Failed to parse HTTP response into NotificationParam: " + e.getMessage(), e);
        }

        try {
            return objectMapper.writeValueAsString(param);
        } catch (IOException e) {
            throw new HapNotificationException("Failed to convert NotificationParam to JSON string: " + e.getMessage(), e);
        }
    }
}