package com.cinemaclub.catalog.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VideoQuality {
    AUTO("auto"),
    P360("360p"),
    P720("720p"),
    P1080("1080p");

    private final String label;

    VideoQuality(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static VideoQuality fromLabel(String label) {
        for (VideoQuality quality : values()) {
            if (quality.label.equalsIgnoreCase(label)) {
                return quality;
            }
        }
        throw new IllegalArgumentException("Unsupported video quality: " + label);
    }
}
