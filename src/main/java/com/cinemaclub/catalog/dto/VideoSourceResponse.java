package com.cinemaclub.catalog.dto;

import com.cinemaclub.catalog.model.VideoQuality;
import com.cinemaclub.catalog.model.VideoType;

public record VideoSourceResponse(VideoQuality quality, VideoType type, String url, int priority) {
}
