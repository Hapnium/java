package com.hapnium.core.models;

import lombok.Data;

/**
 * Configuration parameters required for storage integration (e.g., Cloudinary).
 */
@Data
public class StorageParam {
    /**
     * API key for authentication.
     */
    private String apiKey;

    /**
     * Secret key for authentication.
     */
    private String secretKey;

    /**
     * Storage service name (e.g., cloud provider name).
     */
    private String name;

    /**
     * Whether to use a secure (HTTPS) connection. Defaults to true.
     */
    private Boolean isSecure = Boolean.TRUE;

    /**
     * Custom storage service URL, if applicable.
     */
    private String url;

    /**
     * Whether to show detailed logs.
     */
    private Boolean showLog;
}