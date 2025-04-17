package com.hapnium.core.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FileUploadResponse {
    private String file;
    private String size;
    private String type;
    private String duration;

    @JsonIgnore
    @JsonProperty("asset_id")
    private String assetId;

    @JsonIgnore
    @JsonProperty("public_id")
    private String publicId;
}