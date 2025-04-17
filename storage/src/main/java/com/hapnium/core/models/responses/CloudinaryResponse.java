package com.hapnium.core.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudinaryResponse {
    @JsonProperty("asset_id")
    private String assetId;

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("version")
    private Long version;

    @JsonProperty("version_id")
    private String versionId;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("format")
    private String format;

    @JsonProperty("resource_type")
    private String resourceType;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("pages")
    private Integer pages;

    @JsonProperty("bytes")
    private Long bytes;

    @JsonProperty("type")
    private String type;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("placeholder")
    private Boolean placeholder;

    @JsonProperty("url")
    private String url;

    @JsonProperty("secure_url")
    private String secureUrl;

    @JsonProperty("asset_folder")
    private String assetFolder;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("image_metadata")
    private Map<String, String> imageMetadata;

    @JsonProperty("illustration_score")
    private Double illustrationScore;

    @JsonProperty("semi_transparent")
    private Boolean semiTransparent;

    @JsonProperty("grayscale")
    private Boolean grayscale;

    @JsonProperty("original_filename")
    private String originalFilename;

    @JsonProperty("eager")
    private List<Eager> eager;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Eager {
        private String transformation;
        private Integer width;
        private Integer height;
        private Long bytes;
        private String format;
        private String url;
        private String secureUrl;
    }

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("video")
    private Video video;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Video {
        private String codec;

        @JsonProperty("bit_rate")
        private Long bitRate;

        @JsonProperty("time_base")
        private String timeBase;

        private Integer level;

        @JsonProperty("pix_format")
        private String pixFormat;

        private String profile;
    }

    public static CloudinaryResponse fromJson(Map<String, Object> json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(json, CloudinaryResponse.class);
    }
}