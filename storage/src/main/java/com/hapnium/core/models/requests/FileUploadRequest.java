package com.hapnium.core.models.requests;

import lombok.Data;

/**
 * Represents a single file upload request payload.
 */
@Data
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
     *
     * @return byte array representing the file content.
     */
    public byte[] get() {
        if (getData() != null) {
            return getData();
        }
        return getBytes();
    }

    /**
     * Checks if this file upload details contain data for upload.
     *
     * @return true or false.
     */
    public boolean hasContent() {
        return path.isEmpty() || bytes == null;
    }
}