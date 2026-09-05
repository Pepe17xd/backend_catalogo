package com.cinemaclub.catalog.dto;

import com.cinemaclub.catalog.model.AvailabilityStatus;
import com.cinemaclub.catalog.model.VideoType;
import java.util.List;

public record MoviePlaybackResponse(
        Long movieId,
        String videoUrl,
        List<VideoSourceResponse> videoSources,
        List<SubtitleResponse> subtitles,
        List<String> languages,
        boolean automaticQuality,
        String title,
        StreamingMetadataResponse streamingMetadata,
        VideoType preferredType,
        AvailabilityStatus availabilityStatus
) {
}
