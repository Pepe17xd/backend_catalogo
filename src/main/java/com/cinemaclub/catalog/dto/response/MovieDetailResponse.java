package com.cinemaclub.catalog.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
@Schema(description = "Detalle público de una película")
public record MovieDetailResponse(UUID id, String title, String description, List<String> genres, List<ArtistResponse> artists, Double rating, Integer durationMinutes, String posterUrl, String backdropUrl) {}
