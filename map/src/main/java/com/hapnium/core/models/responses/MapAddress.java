package com.hapnium.core.models.responses;

import com.hapnium.core.models.BaseLocation;
import lombok.Data;

import java.util.List;

@Data
public class MapAddress {
    private String formattedAddress;
    private List<AddressComponent> addressComponents;
    private BaseLocation location;
    private DisplayName displayName;
    private String shortFormattedAddress;

    @Data
    public static class AddressComponent {
        private String longText;
        private String shortText;
        private List<String> types;
        private String languageCode;
    }

    @Data
    public static class DisplayName {
        private String text;
        private String languageCode;
    }
}