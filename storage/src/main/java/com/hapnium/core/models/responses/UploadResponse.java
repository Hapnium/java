package com.hapnium.core.models.responses;

import lombok.Data;

@Data
public class UploadResponse {
    private String type;
    private String id;
    private FileUploadResponse file;
    private CloudinaryResponse cloud;
}