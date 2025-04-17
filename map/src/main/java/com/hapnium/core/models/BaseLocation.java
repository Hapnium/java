package com.hapnium.core.models;

import lombok.Data;

@Data
public class BaseLocation {
    private Double latitude;
    private Double longitude;
    private String place;

    public BaseLocation() {}

    public BaseLocation(Double latitude, Double longitude, String place) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
    }

    public BaseLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}