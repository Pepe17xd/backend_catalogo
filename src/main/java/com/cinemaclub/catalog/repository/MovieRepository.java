package com.cinemaclub.catalog.repository;

import com.cinemaclub.catalog.model.Movie;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Override
    @EntityGraph(attributePaths = {"videoSources", "subtitles"})
    List<Movie> findAll();

    @Override
    @EntityGraph(attributePaths = {"videoSources", "subtitles"})
    Optional<Movie> findById(Long id);

    @EntityGraph(attributePaths = {"videoSources", "subtitles"})
    List<Movie> findByGenreContainingIgnoreCase(String genre);
}
