package com.hapnium.core;

import com.hapnium.core.models.requests.UploadRequest;
import com.hapnium.core.models.responses.UploadResponse;

/**
 * Interface for file storage services that support uploading and deleting files.
 */
interface StorageService {
    /**
     * Uploads a file to the cloud storage provider using the given request.
     *
     * @param request The {@link UploadRequest} object representing the file to be uploaded.
     * @return An {@link UploadResponse} containing metadata about the uploaded file.
     */
    UploadResponse upload(UploadRequest request);

    /**
     * Deletes a file from the storage provider using its key.
     *
     * @param key The public ID or key of the file to delete.
     * @return {@code true} if the file was successfully deleted; {@code false} otherwise.
     */
    boolean delete(String key);
}