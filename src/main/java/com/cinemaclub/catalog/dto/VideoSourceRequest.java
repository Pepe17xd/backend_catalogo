package com.cinemaclub.catalog.dto;

import com.cinemaclub.catalog.model.VideoQuality;
import com.cinemaclub.catalog.model.VideoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record VideoSourceRequest(
        @NotNull(message = "video source quality is required") VideoQuality quality,
        VideoType type,
        @NotBlank(message = "video source URL is required") String url,
        @PositiveOrZero(message = "video source priority must be zero or greater") Integer priority
) {
}
