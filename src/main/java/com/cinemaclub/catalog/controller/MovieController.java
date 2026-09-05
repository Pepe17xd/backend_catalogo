package com.cinemaclub.catalog.controller;

import com.cinemaclub.catalog.dto.MovieRequest;
import com.cinemaclub.catalog.dto.MovieResponse;
import com.cinemaclub.catalog.dto.MoviePlaybackResponse;
import com.cinemaclub.catalog.service.MovieService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog/movies")
@CrossOrigin("*")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieResponse> all() {
        return movieService.findAll();
    }

    @GetMapping("/{id}")
    public MovieResponse one(@PathVariable Long id) {
        return movieService.findById(id);
    }

    @GetMapping("/{id}/play")
    public MoviePlaybackResponse play(@PathVariable Long id) {
        return movieService.findPlaybackById(id);
    }

    @GetMapping("/search")
    public List<MovieResponse> search(@RequestParam String genre) {
        return movieService.findByGenre(genre);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse create(@Valid @RequestBody MovieRequest request) {
        return movieService.create(request);
    }
}
