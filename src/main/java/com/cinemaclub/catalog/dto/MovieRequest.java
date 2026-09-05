package com.cinemaclub.catalog.dto;

import com.cinemaclub.catalog.model.AvailabilityStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import java.util.List;

public record MovieRequest(
        @NotBlank(message = "title is required") String title,
        @NotBlank(message = "genre is required") String genre,
        String director,
        @Min(value = 1888, message = "releaseYear must be 1888 or later")
        @Max(value = 2100, message = "releaseYear must not exceed 2100") Integer releaseYear,
        @Size(max = 2000, message = "description must not exceed 2000 characters") String description,
        Double rating,
        @NotBlank(message = "videoUrl is required") String videoUrl,
        String posterUrl,
        String backdropUrl,
        @Positive(message = "duration must be greater than zero") Integer duration,
        Boolean active,
        AvailabilityStatus availabilityStatus,
        @Valid List<VideoSourceRequest> videoSources,
        @Valid List<SubtitleRequest> subtitles
) {
}
