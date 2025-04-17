package com.hapnium.core;

import com.cloudinary.Cloudinary;
import com.cloudinary.Configuration;
import com.cloudinary.utils.ObjectUtils;
import com.hapnium.core.exception.HapStorageException;
import com.hapnium.core.models.StorageParam;
import com.hapnium.core.models.requests.FileUploadRequest;
import com.hapnium.core.models.requests.UploadRequest;
import com.hapnium.core.models.responses.CloudinaryResponse;
import com.hapnium.core.models.responses.FileUploadResponse;
import com.hapnium.core.models.responses.UploadResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;


/**
 * Storage implementation using Cloudinary as the backend service.
 * Handles secure file uploads and deletions with optional metadata enrichment.
 */
@Slf4j
class Storage implements StorageService {
    private final Cloudinary cloud;
    private final boolean showLog;

    /**
     * Constructs a new {@code Storage} instance using the given storage parameters.
     *
     * @param param A {@link StorageParam} containing credentials and settings.
     * @throws HapStorageException if any required configuration is missing.
     */
    Storage(StorageParam param) {
        if(param == null) {
            throw new HapStorageException("Storage param is null");
        }

        if(param.getIsSecure() == null) {
            throw new HapStorageException("Define whether security is important or not");
        }

        if(param.getUrl() == null || param.getUrl().isEmpty()) {
            Configuration config = new Configuration();
            config.apiKey = param.getApiKey();
            config.secure = param.getIsSecure();
            config.apiSecret = param.getSecretKey();
            config.cloudName = param.getName();

            this.cloud = new Cloudinary(config);
        } else {
            this.cloud = new Cloudinary(param.getUrl());
        }

        this.showLog = param.getShowLog() != null ? param.getShowLog() : false;

        if(param.getShowLog() != null && param.getShowLog()) {
            log.info("STORAGE SDK INITIALIZER::: Cloudinary Initialized for {}", this.cloud.config.cloudName);
            log.info("STORAGE SDK INITIALIZER::: Cloudinary Initialized with {}", this.cloud.config.cname);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public UploadResponse upload(UploadRequest request) {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("asset_folder", request.getFolder());
            params.put("use_asset_folder_as_public_id_prefix", true);

            if (isVideo(request.getUpload().getPath()) || isAudio(request.getUpload().getPath()) || isImage(request.getUpload().getPath())) {
                params.put("display_name", generateName(request.getUpload().getPath()));
            } else {
                params.put("use_filename_as_display_name", true);
            }

            if(isVideo(request.getUpload().getPath())) {
                params.put("resource_type", "video");
            }

            var result = cloud.uploader().upload(request.getUpload().get(), params);

            return getUploadResponse(request, CloudinaryResponse.fromJson(result));
        } catch (Exception e) {
            if(showLog) {
                log.error(e.getMessage(), e);
            }

            throw new HapStorageException("An error occurred while uploading your %s".formatted(getType(request.getUpload().getPath())), e);
        }
    }

    /**
     * Builds the {@link UploadResponse} using Cloudinary response data and request metadata.
     *
     * @param request The original upload request.
     * @param json The response from Cloudinary.
     * @return An {@link UploadResponse} with file and cloud metadata.
     */
    private UploadResponse getUploadResponse(UploadRequest request, CloudinaryResponse json) {
        FileUploadResponse upload = copyWith(json.getSecureUrl(), request.getUpload());
        upload.setPublicId(json.getPublicId());
        upload.setAssetId(json.getAssetId());

        UploadResponse response = new UploadResponse();
        response.setFile(upload);
        response.setCloud(json);
        response.setType(request.getType());
        response.setId(request.getId());

        return response;
    }

    /**
     * Creates a {@link FileUploadResponse} object from the given {@link FileUploadRequest}.
     * This method populates the response object with file details such as path, duration,
     * size, and type.
     *
     * @param file The {@link FileUploadRequest} object.
     * @return A {@link FileUploadResponse} object.
     */
    private FileUploadResponse copyWith(String result, FileUploadRequest file) {
        FileUploadResponse response = new FileUploadResponse();
        response.setFile(result);
        response.setDuration(file.getDuration());
        response.setSize(file.getSize());
        response.setType(getType(file.getPath()));

        return response;
    }

    @Override
    public boolean delete(String key) {
        try {
            cloud.uploader().destroy(key, ObjectUtils.emptyMap());

            return true;
        } catch (IOException e) {
            if(showLog) {
                log.error(e.getMessage(), e);
            }

            throw new HapStorageException("An error occurred while deleting your file", e);
        }
    }

    // Utility methods for file type detection (same as your original implementation)
    /**
     * Generates a unique identifier for the uploaded file.
     *
     * @return The generated unique identifier.
     */
    private String generateName(String path) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);

        String prefix = "S"; // Default prefix
        if (isVideo(path)) {
            prefix += "VID";
        } else {
            prefix += "IMG";
        }

        return String.format("%s-%s-%s", prefix, timestamp, uniqueId);
    }

    /**
     * Determines the file type based on the file extension.
     *
     * @param path The file path.
     * @return The file type (e.g., "image", "video", "audio", "document", "other").
     */
    private String getType(String path) {
        String ext = path.toLowerCase();

        if (isVideo(ext)) {
            return "video";
        } else if (isImage(ext)) {
            return "photo";
        } else if(isVector(ext)) {
            return "svg";
        } else if (isAudio(ext)) {
            return "audio";
        } else if (isDocument(ext)) {
            return "document";
        } else if(isAPK(ext)) {
            return "apk";
        } else if(isHTML(ext)) {
            return "html";
        } else {
            return "other";
        }
    }

    /**
     * Checks if the given file extension is a video file extension.
     *
     * @param ext The file extension (e.g., ".mp4", ".avi").
     * @return `true` if the extension is a video extension, `false` otherwise.
     */
    private boolean isVideo(String ext) {
        return ext.endsWith(".mp4") ||
                ext.endsWith(".avi") ||
                ext.endsWith(".wmv") ||
                ext.endsWith(".rmvb") ||
                ext.endsWith(".mpg") ||
                ext.endsWith(".mpeg") ||
                ext.endsWith(".3gp");
    }

    /**
     * Checks if the given file extension is an image file extension.
     *
     * @param ext The file extension (e.g., ".jpg", ".png").
     * @return `true` if the extension is an image extension, `false` otherwise.
     */
    private boolean isImage(String ext) {
        return ext.endsWith(".jpg") ||
                ext.endsWith(".jpeg") ||
                ext.endsWith(".png") ||
                ext.endsWith(".gif") ||
                ext.endsWith(".bmp");
    }

    /**
     * Checks if the given file extension is an audio file extension.
     *
     * @param ext The file extension (e.g., ".mp3", ".wav").
     * @return `true` if the extension is an audio extension, `false` otherwise.
     */
    private boolean isAudio(String ext) {
        return ext.endsWith(".mp3") ||
                ext.endsWith(".wav") ||
                ext.endsWith(".wma") ||
                ext.endsWith(".amr") ||
                ext.endsWith(".ogg");
    }

    /**
     * Checks if the given file extension is a document file extension.
     *
     * @param ext The file extension (e.g., ".pdf", ".doc", ".docx").
     * @return `true` if the extension is a document extension, `false` otherwise.
     */
    private boolean isDocument(String ext) {
        return isPDF(ext) || isPPT(ext) || isWord(ext) || isExcel(ext) || isTxt(ext) || isChm(ext);
    }

    /**
     * Checks if the given file extension is a PowerPoint file extension.
     *
     * @param ext The file extension (e.g., ".ppt", ".pptx").
     * @return `true` if the extension is a PowerPoint extension, `false` otherwise.
     */
    private boolean isPPT(String ext) {
        return ext.endsWith(".ppt") || ext.endsWith(".pptx");
    }

    /**
     * Checks if the given file extension is a Word file extension.
     *
     * @param ext The file extension (e.g., ".doc", ".docx").
     * @return `true` if the extension is a Word extension, `false` otherwise.
     */
    private boolean isWord(String ext) {
        return ext.endsWith(".doc") || ext.endsWith(".docx");
    }

    /**
     * Checks if the given file extension is an Excel file extension.
     *
     * @param ext The file extension (e.g., ".xls", ".xlsx").
     * @return `true` if the extension is an Excel extension, `false` otherwise.
     */
    private boolean isExcel(String ext) {
        return ext.endsWith(".xls") || ext.endsWith(".xlsx");
    }

    /**
     * Checks if the given file extension is an APK file extension.
     *
     * @param ext The file extension (e.g., ".apk").
     * @return `true` if the extension is an APK extension, `false` otherwise.
     */
    private boolean isAPK(String ext) {
        return ext.toLowerCase().endsWith(".apk");
    }

    /**
     * Checks if the given file extension is a PDF file extension.
     *
     * @param ext The file extension (e.g., ".pdf").
     * @return `true` if the extension is a PDF extension, `false` otherwise.
     */
    private boolean isPDF(String ext) {
        return ext.toLowerCase().endsWith(".pdf");
    }

    /**
     * Checks if the given file extension is a TXT file extension.
     *
     * @param ext The file extension (e.g., ".txt").
     * @return `true` if the extension is a TXT extension, `false` otherwise.
     */
    private boolean isTxt(String ext) {
        return ext.toLowerCase().endsWith(".txt");
    }

    /**
     * Checks if the given file extension is a CHM file extension.
     *
     * @param ext The file extension (e.g., ".chm").
     * @return `true` if the extension is a CHM extension, `false` otherwise.
     */
    private boolean isChm(String ext) {
        return ext.toLowerCase().endsWith(".chm");
    }

    /**
     * Checks if the given file extension is a vector file extension.
     *
     * @param ext The file extension (e.g., ".svg").
     * @return `true` if the extension is a vector extension, `false` otherwise.
     */
    private boolean isVector(String ext) {
        return ext.toLowerCase().endsWith(".svg");
    }

    /**
     * Checks if the given file extension is an HTML file extension.
     *
     * @param ext The file extension (e.g., ".html").
     * @return `true` if the extension is an HTML extension, `false` otherwise.
     */
    private boolean isHTML(String ext) {
        return ext.toLowerCase().endsWith(".html");
    }
}