package com.cinemaclub.catalog.dto;

import com.cinemaclub.catalog.model.AvailabilityStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record MovieRequest(
        @NotBlank(message = "title is required") String title,
        @Size(max = 2000) String description,
        @Min(1888) @Max(2100) Integer releaseYear,
        @NotNull @Positive(message = "durationMinutes must be greater than zero") Integer durationMinutes,
        String posterUrl,
        String backdropUrl,
        @DecimalMin("0.0") @DecimalMax("10.0") Double rating,
        AvailabilityStatus status,
        List<String> genreSlugs,
        List<Long> artistIds,
        @Valid List<VideoSourceRequest> videoSources,
        @Valid List<SubtitleRequest> subtitles) {}
