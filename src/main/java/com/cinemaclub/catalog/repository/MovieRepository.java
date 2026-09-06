package com.cinemaclub.catalog.repository;

import com.cinemaclub.catalog.model.Movie;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @EntityGraph(attributePaths = "genres")
    @Query("select distinct m from Movie m")
    List<Movie> findAllForCatalog();

    @EntityGraph(attributePaths = {"genres", "artists", "videoSources", "subtitles"})
    Optional<Movie> findByPublicId(UUID publicId);

    @EntityGraph(attributePaths = "genres")
    @Query("""
            select distinct m from Movie m left join m.genres g left join m.artists a
            where lower(m.title) like lower(concat('%', :query, '%'))
               or lower(g.name) like lower(concat('%', :query, '%'))
               or lower(a.name) like lower(concat('%', :query, '%'))
            """)
    List<Movie> search(@Param("query") String query);
}
