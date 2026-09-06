package com.cinemaclub.catalog.repository;
import com.cinemaclub.catalog.model.Genre;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GenreRepository extends JpaRepository<Genre, Long> { List<Genre> findBySlugIn(Collection<String> slugs); }
