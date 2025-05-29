package com.hapnium.core.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private String type;
    private String id;
    private FileUploadResponse file;
    private CloudinaryResponse cloud;
}