package com.cinemaclub.catalog.service;

import com.cinemaclub.catalog.dto.response.*;
import com.cinemaclub.catalog.model.*;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    public MovieCatalogResponse toCatalog(Movie movie) {
        return new MovieCatalogResponse(movie.getPublicId(), movie.getTitle(), movie.getPosterUrl(), movie.getBackdropUrl(),
                movie.getGenres().stream().map(Genre::getName).sorted().toList(), movie.getRating(), movie.getDurationMinutes());
    }
    public MovieDetailResponse toDetail(Movie movie) {
        return new MovieDetailResponse(movie.getPublicId(), movie.getTitle(), movie.getDescription(),
                movie.getGenres().stream().map(Genre::getName).sorted().toList(),
                movie.getArtists().stream().sorted(Comparator.comparing(Artist::getName)).map(a -> new ArtistResponse(a.getName(), a.getBiography(), a.getPhotoUrl(), a.getType())).toList(),
                movie.getRating(), movie.getDurationMinutes(), movie.getPosterUrl(), movie.getBackdropUrl());
    }
    public MovieSessionResponse toSession(Movie movie) {
        MovieVideoSource source = movie.getVideoSources().stream().min(Comparator.comparingInt(MovieVideoSource::getPriority))
                .orElseThrow(() -> new IllegalStateException("Movie has no video source configured"));
        List<MovieSessionResponse.SessionSubtitleResponse> subtitles = movie.getSubtitles().stream()
                .sorted(Comparator.comparing(MovieSubtitle::getLanguage))
                .map(s -> new MovieSessionResponse.SessionSubtitleResponse(s.getLanguage(), s.getUrl())).toList();
        return new MovieSessionResponse(movie.getPublicId(), movie.getTitle(), movie.getDurationMinutes(),
                new MovieSessionResponse.StreamResponse(source.getUrl(), source.getType()), subtitles);
    }
}
