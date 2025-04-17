package com.hapnium.core.models.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class PlacesResponse {
    private List<Place> places;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Place extends MapAddress {
        private String id;
        private String nationalPhoneNumber;
        private String internationalPhoneNumber;
        private String googleMapsUri;
        private String businessStatus;
        private String iconMaskBaseUri;
        private Long userRatingCount;
        private Double rating;
        private OpeningHours currentOpeningHours;
        private Description editorialSummary;
        private GenerativeSummary generativeSummary;
        private GoogleMapsLinks googleMapsLinks;

        @Data
        public static class OpeningHours {
            private Boolean openNow;
        }

        @Data
        public static class GenerativeSummary {
            private Description description;
            private Description overview;
        }

        @Data
        public static class Description {
            private String text;
        }

        @Data
        public static class GoogleMapsLinks {
            private String directionsUri;
            private String placeUri;
            private String writeAReviewUri;
            private String reviewsUri;
            private String photosUri;
        }
    }
}