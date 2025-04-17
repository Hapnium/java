package com.hapnium.core;

import com.hapnium.core.models.StorageParam;
import com.hapnium.core.models.requests.UploadRequest;
import com.hapnium.core.models.responses.UploadResponse;

/**
 * {@code HapStorage} serves as a public interface for interacting with the underlying
 * {@link Storage} implementation that manages file uploads and deletions using Cloudinary.
 *
 * <p>This class delegates all storage-related actions to the {@link Storage} instance and
 * provides a simplified entry point for clients to integrate file handling capabilities.
 */
public class HapStorage implements StorageService {
    private final StorageService delegate;

    /**
     * Constructs a new instance of {@code HapStorage} with the provided {@link StorageParam}.
     *
     * @param param The configuration parameters used to initialize the storage backend.
     * @throws com.hapnium.core.exception.HapStorageException if {@code param} is invalid.
     */
    public HapStorage(StorageParam param) {
        this.delegate = new Storage(param);
    }

    /**
     * For testing purpose
     *
     * @param delegate The {@link StorageService} to use
     */
    HapStorage(StorageService delegate) {
        this.delegate = delegate;
    }

    @Override
    public UploadResponse upload(UploadRequest request) {
        return delegate.upload(request);
    }

    @Override
    public boolean delete(String key) {
        return delegate.delete(key);
    }
}