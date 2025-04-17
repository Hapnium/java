package com.hapnium.core;

import com.hapnium.core.models.NotificationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HapNotificationTest {
    private HapNotification mockService;

    @BeforeEach
    void setUp() {
        mockService = mock(HapNotification.class);
    }

    @Test
    void testSendNotificationReturnsMessageId() {
        // Given
        NotificationMessage<String> message = new NotificationMessage<>();
        message.setToken("some-device-token");

        NotificationMessage.Details<String> details = new NotificationMessage.Details<>();
        details.setTitle("Hello");
        details.setBody("World");
        details.setData("Payload");

        message.setData(details);
        message.setCategory("ALERT");

        String expectedMessageId = "firebase-message-id-123";

        when(mockService.send(any())).thenReturn(expectedMessageId);

        // When
        String actualMessageId = mockService.send(message);

        // Then
        assertEquals(expectedMessageId, actualMessageId);
        verify(mockService, times(1)).send(any());
    }
}