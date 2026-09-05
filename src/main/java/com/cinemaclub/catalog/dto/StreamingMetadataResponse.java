package com.cinemaclub.catalog.dto;

import java.util.List;

public record StreamingMetadataResponse(
        String mimeType,
        boolean adaptive,
        boolean requiresMediaSource,
        List<String> compatiblePlayers
) {
}
