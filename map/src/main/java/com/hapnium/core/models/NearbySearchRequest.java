package com.hapnium.core.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class NearbySearchRequest {
    private ArrayList<String> includedTypes = new ArrayList<>();
    private ArrayList<String> excludedTypes;
    private Integer maxResultCount;
    private LocationRestriction locationRestriction = new LocationRestriction();
    private String rankPreference = "DISTANCE";

    @Data
    public static class LocationRestriction {
        private LocationRestrictionCircle circle = new LocationRestrictionCircle();

        @Override
        public String toString() {
            return "LocationRestriction{" + "circle=" + circle + '}';
        }
    }

    @Data
    public static class LocationRestrictionCircle {
        private LocationRestrictionCircleCenter center = new LocationRestrictionCircleCenter();
        private Double radius = 500.0;

        @Override
        public String toString() {
            return "LocationRestrictionCircle{" +
                    "center=" + center +
                    ", radius=" + radius +
                    '}';
        }
    }

    @Data
    public static class LocationRestrictionCircleCenter {
        private Double latitude;
        private Double longitude;

        @Override
        public String toString() {
            return "LocationRestrictionCircleCenter{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NearbySearchRequest{" +
                "includedTypes=" + includedTypes +
                ", excludedTypes=" + excludedTypes +
                ", maxResultCount=" + maxResultCount +
                ", locationRestriction=" + locationRestriction +
                ", rankPreference='" + rankPreference + '\'' +
                '}';
    }
}