package com.cinemaclub.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record SubtitleRequest(
        @NotBlank(message = "subtitle language is required") String language,
        @NotBlank(message = "subtitle URL is required") String url
) {
}
