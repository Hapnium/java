package com.hapnium.core.models.requests;

import lombok.Data;

/**
 * Represents a request to upload a file.
 */
@Data
public class UploadRequest {
    /**
     * The type of the upload (e.g., image, video, etc.).
     */
    private String type;

    /**
     * The unique identifier associated with the upload.
     */
    private String id;

    /**
     * The folder path where the file should be stored.
     */
    private String folder;

    /**
     * The file upload details.
     */
    private FileUploadRequest upload;
}