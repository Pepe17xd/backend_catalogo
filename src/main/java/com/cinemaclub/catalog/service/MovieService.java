package com.cinemaclub.catalog.service;

import com.cinemaclub.catalog.dto.MovieRequest;
import com.cinemaclub.catalog.dto.MovieResponse;
import com.cinemaclub.catalog.dto.MoviePlaybackResponse;
import com.cinemaclub.catalog.dto.VideoSourceResponse;
import com.cinemaclub.catalog.dto.SubtitleResponse;
import com.cinemaclub.catalog.dto.StreamingMetadataResponse;
import com.cinemaclub.catalog.model.AvailabilityStatus;
import com.cinemaclub.catalog.model.Movie;
import com.cinemaclub.catalog.model.MovieVideoSource;
import com.cinemaclub.catalog.model.MovieSubtitle;
import com.cinemaclub.catalog.model.VideoType;
import com.cinemaclub.catalog.repository.MovieRepository;
import java.util.List;
import java.util.Comparator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> findAll() {
        return movieRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MovieResponse findById(Long id) {
        return toResponse(findMovieById(id));
    }

    @Transactional(readOnly = true)
    public MoviePlaybackResponse findPlaybackById(Long id) {
        Movie movie = findMovieById(id);
        List<VideoSourceResponse> sources = toVideoSources(movie);
        List<SubtitleResponse> subtitles = toSubtitles(movie);
        List<String> languages = subtitles.stream()
                .map(SubtitleResponse::language)
                .distinct()
                .sorted()
                .toList();
        boolean automaticQuality = movie.getVideoSources().stream()
                .anyMatch(source -> source.getQuality() == com.cinemaclub.catalog.model.VideoQuality.AUTO);
        VideoType preferredType = sources.isEmpty() ? VideoType.MP4 : sources.get(0).type();
        AvailabilityStatus availabilityStatus = !movie.isActive()
                ? AvailabilityStatus.OFFLINE
                : movie.getAvailabilityStatus();

        return new MoviePlaybackResponse(
                movie.getId(), movie.getVideoUrl(), sources, subtitles, languages, automaticQuality,
                movie.getTitle(), streamingMetadata(preferredType), preferredType, availabilityStatus);
    }

    @Transactional(readOnly = true)
    public List<MovieResponse> findByGenre(String genre) {
        return movieRepository.findByGenreContainingIgnoreCase(genre).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MovieResponse create(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.title());
        movie.setGenre(request.genre());
        movie.setDirector(request.director());
        movie.setReleaseYear(request.releaseYear());
        movie.setDescription(request.description());
        movie.setRating(request.rating());
        movie.setVideoUrl(request.videoUrl());
        movie.setPosterUrl(request.posterUrl());
        movie.setBackdropUrl(request.backdropUrl());
        movie.setDuration(request.duration());
        movie.setActive(request.active() == null || request.active());
        movie.setAvailabilityStatus(request.availabilityStatus() == null
                ? AvailabilityStatus.READY : request.availabilityStatus());
        if (request.videoSources() != null) {
            request.videoSources().forEach(source -> movie.addVideoSource(
                    new MovieVideoSource(source.quality(),
                            source.type() == null ? VideoType.MP4 : source.type(), source.url(),
                            source.priority() == null ? 0 : source.priority())));
        }
        if (request.subtitles() != null) {
            request.subtitles().forEach(subtitle -> movie.addSubtitle(
                    new MovieSubtitle(subtitle.language(), subtitle.url())));
        }
        return toResponse(movieRepository.save(movie));
    }

    private MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getId(), movie.getTitle(), movie.getGenre(), movie.getDirector(),
                movie.getReleaseYear(), movie.getDescription(), movie.getRating(),
                movie.getVideoUrl(), movie.getPosterUrl(), movie.getBackdropUrl(),
                movie.getDuration(), movie.isActive(),
                toVideoSources(movie), toSubtitles(movie));
    }

    private Movie findMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }

    private List<VideoSourceResponse> toVideoSources(Movie movie) {
        return movie.getVideoSources().stream()
                .sorted(Comparator.comparingInt(MovieVideoSource::getPriority))
                .map(source -> new VideoSourceResponse(
                        source.getQuality(), source.getType(), source.getUrl(), source.getPriority()))
                .toList();
    }

    private List<SubtitleResponse> toSubtitles(Movie movie) {
        return movie.getSubtitles().stream()
                .map(subtitle -> new SubtitleResponse(
                        subtitle.getId(), subtitle.getLanguage(), subtitle.getUrl()))
                .toList();
    }

    private StreamingMetadataResponse streamingMetadata(VideoType type) {
        return switch (type) {
            case HLS -> new StreamingMetadataResponse(
                    "application/vnd.apple.mpegurl", true, true,
                    List.of("VIDEO_JS", "HLS_JS", "SHAKA_PLAYER"));
            case DASH -> new StreamingMetadataResponse(
                    "application/dash+xml", true, true,
                    List.of("VIDEO_JS", "SHAKA_PLAYER"));
            case MP4 -> new StreamingMetadataResponse(
                    "video/mp4", false, false,
                    List.of("VIDEO_JS", "SHAKA_PLAYER"));
        };
    }
}
