package com.hapnium.core.models.responses;

import lombok.Data;

import java.util.List;

@Data
public class MapSuggestionsResponse {
    private List<Suggestion> suggestions;

    @Data
    public static class Suggestion {
        private PlacePrediction placePrediction;
    }

    @Data
    public static class PlacePrediction {
        private String placeId;
    }
}