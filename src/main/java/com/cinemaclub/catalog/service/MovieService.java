package com.cinemaclub.catalog.service;

import com.cinemaclub.catalog.dto.*;
import com.cinemaclub.catalog.dto.response.*;
import com.cinemaclub.catalog.exception.ResourceNotFoundException;
import com.cinemaclub.catalog.model.*;
import com.cinemaclub.catalog.repository.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {
    private static final int SECTION_SIZE = 20;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final MovieMapper mapper;

    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository, ArtistRepository artistRepository, MovieMapper mapper) {
        this.movieRepository = movieRepository; this.genreRepository = genreRepository; this.artistRepository = artistRepository; this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<MovieCatalogResponse> findAll() { return movieRepository.findAllForCatalog().stream().map(mapper::toCatalog).toList(); }

    @Transactional(readOnly = true)
    public MovieDetailResponse findDetail(UUID publicId) { return mapper.toDetail(findMovie(publicId)); }

    @Transactional(readOnly = true)
    public MovieSessionResponse findSessionInfo(UUID publicId) { return mapper.toSession(findMovie(publicId)); }

    @Transactional(readOnly = true)
    public List<MovieCatalogResponse> search(String query) {
        if (query == null || query.isBlank()) throw new IllegalArgumentException("q must not be blank");
        return movieRepository.search(query.trim()).stream().map(mapper::toCatalog).toList();
    }

    @Transactional(readOnly = true)
    public HomeResponse home() {
        List<Movie> movies = movieRepository.findAllForCatalog();
        Comparator<Movie> byRating = Comparator.comparing(Movie::getRating, Comparator.nullsLast(Comparator.reverseOrder()));
        MovieCatalogResponse featured = movies.stream().sorted(byRating).findFirst().map(mapper::toCatalog).orElse(null);
        List<MovieCatalogResponse> trending = movies.stream().sorted(byRating).limit(SECTION_SIZE).map(mapper::toCatalog).toList();
        List<MovieCatalogResponse> action = movies.stream().filter(m -> m.getGenres().stream().anyMatch(g -> g.getSlug().equalsIgnoreCase("action"))).limit(SECTION_SIZE).map(mapper::toCatalog).toList();
        return new HomeResponse(featured, List.of(new HomeSectionResponse("Trending", trending), new HomeSectionResponse("Action", action)));
    }

    @Transactional
    public MovieDetailResponse create(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.title()); movie.setDescription(request.description()); movie.setReleaseYear(request.releaseYear());
        movie.setDurationMinutes(request.durationMinutes()); movie.setPosterUrl(request.posterUrl()); movie.setBackdropUrl(request.backdropUrl());
        movie.setRating(request.rating()); movie.setStatus(request.status() == null ? AvailabilityStatus.READY : request.status());
        if (request.genreSlugs() != null) movie.getGenres().addAll(genreRepository.findBySlugIn(request.genreSlugs()));
        if (request.artistIds() != null) movie.getArtists().addAll(artistRepository.findByIdIn(request.artistIds()));
        if (request.videoSources() != null) request.videoSources().forEach(s -> movie.addVideoSource(new MovieVideoSource(s.quality(), s.type() == null ? VideoType.MP4 : s.type(), s.url(), s.priority() == null ? 0 : s.priority())));
        if (request.subtitles() != null) request.subtitles().forEach(s -> movie.addSubtitle(new MovieSubtitle(s.language(), s.url())));
        return mapper.toDetail(movieRepository.save(movie));
    }

    private Movie findMovie(UUID publicId) { return movieRepository.findByPublicId(publicId).orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + publicId)); }
}
