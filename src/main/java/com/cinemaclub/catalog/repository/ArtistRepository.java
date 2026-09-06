package com.cinemaclub.catalog.repository;
import com.cinemaclub.catalog.model.Artist;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ArtistRepository extends JpaRepository<Artist, Long> { List<Artist> findByIdIn(Collection<Long> ids); }
