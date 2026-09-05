package com.cinemaclub.catalog.dto;

import java.util.List;

public record MovieResponse(
        Long id,
        String title,
        String genre,
        String director,
        Integer releaseYear,
        String description,
        Double rating,
        String videoUrl,
        String posterUrl,
        String backdropUrl,
        Integer duration,
        boolean active,
        List<VideoSourceResponse> videoSources,
        List<SubtitleResponse> subtitles
) {
}
