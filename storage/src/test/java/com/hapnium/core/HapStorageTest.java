package com.hapnium.core;

import com.hapnium.core.models.requests.FileUploadRequest;
import com.hapnium.core.models.requests.UploadRequest;
import com.hapnium.core.models.responses.FileUploadResponse;
import com.hapnium.core.models.responses.UploadResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HapStorageTest {
    private StorageService storageService;
    private HapStorage hapStorage;

    @BeforeEach
    void setup() {
        storageService = mock(StorageService.class);
        hapStorage = new HapStorage(storageService);
    }

    @Test
    void upload_ShouldReturnMockedResponse() {
        // Arrange
        UploadRequest request = new UploadRequest();
        FileUploadRequest file = new FileUploadRequest();
        file.setPath("test.jpg");
        file.setSize("1024mb");
        request.setUpload(file);

        UploadResponse expectedResponse = new UploadResponse();
        FileUploadResponse fileResponse = new FileUploadResponse();
        fileResponse.setFile("https://res.cloudinary.com/demo/image/upload/test.jpg");
        expectedResponse.setFile(fileResponse);

        when(storageService.upload(any(UploadRequest.class))).thenReturn(expectedResponse);

        // Act
        UploadResponse response = hapStorage.upload(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getFile());
        assertEquals("https://res.cloudinary.com/demo/image/upload/test.jpg", response.getFile().getFile());
    }

    @Test
    void delete_ShouldReturnTrue_WhenFileExists() {
        // Arrange
        when(storageService.delete(anyString())).thenReturn(true);

        // Act
        boolean deleted = hapStorage.delete("some-public-id");

        // Assert
        assertTrue(deleted);
    }
}