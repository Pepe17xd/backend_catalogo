package com.cinemaclub.catalog.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
@Schema(description = "Resumen de una película para listados")
public record MovieCatalogResponse(UUID id, String title, String posterUrl, String backdropUrl, List<String> genres, Double rating, Integer durationMinutes) {}
