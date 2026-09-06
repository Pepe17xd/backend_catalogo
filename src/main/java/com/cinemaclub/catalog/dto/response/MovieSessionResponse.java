package com.cinemaclub.catalog.dto.response;
import com.cinemaclub.catalog.model.VideoType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
@Schema(description = "Contrato de reproducción para Cinema Session Service")
public record MovieSessionResponse(UUID movieId, String title, Integer durationMinutes, StreamResponse stream, List<SessionSubtitleResponse> subtitles) {
    public record StreamResponse(String url, VideoType type) {}
    public record SessionSubtitleResponse(String language, String url) {}
}
