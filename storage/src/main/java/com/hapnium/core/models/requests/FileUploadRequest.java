package com.hapnium.core.models.requests;

import com.hapnium.core.exception.HapStorageException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a single file upload request payload.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadRequest {
    /**
     * Path to the file on the file system.
     */
    private String path;

    /**
     * Base64 encoded media string (optional).
     */
    private String media;

    /**
     * Size of the file (in bytes, as a String).
     */
    private String size;

    /**
     * Duration of the media (e.g., video length in seconds).
     */
    private String duration;

    /**
     * Raw file bytes (e.g., from disk).
     */
    private byte[] bytes;

    /**
     * In-memory file data (e.g., uploaded directly).
     */
    private byte[] data;

    /**
     * Returns the best available data source (data or bytes).
     * If none is provided but the path is provided, it will generate the byte from the path
     *
     * @return byte array representing the file content.
     */
    public byte[] get() {
        if (data != null && data.length > 0) {
            return data;
        }

        if (bytes != null && bytes.length > 0) {
            return bytes;
        }

        if (path != null && !path.isEmpty()) {
            try {
                Path filePath = Paths.get(path);
                bytes = Files.readAllBytes(filePath); // Cache result to avoid multiple reads

                return bytes;
            } catch (IOException e) {
                throw new HapStorageException("Failed to read file from path: " + path, e);
            }
        }

        return null;
    }

    /**
     * Checks if this file upload details contain data for upload.
     *
     * @return true or false.
     */
    public boolean hasContent() {
        byte[] result = get();

        return !path.isEmpty() || (result != null && result.length > 0);
    }
}