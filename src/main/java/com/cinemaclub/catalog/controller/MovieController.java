package com.cinemaclub.catalog.controller;

import com.cinemaclub.catalog.dto.MovieRequest;
import com.cinemaclub.catalog.dto.response.*;
import com.cinemaclub.catalog.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog/movies")
@Tag(name = "Movies", description = "Catálogo público y contrato para sesiones")
public class MovieController {
    private final MovieService movieService;
    public MovieController(MovieService movieService) { this.movieService = movieService; }

    @GetMapping @Operation(summary = "Lista el catálogo")
    public List<MovieCatalogResponse> all() { return movieService.findAll(); }

    @GetMapping("/{publicId}") @Operation(summary = "Obtiene el detalle por UUID público")
    public MovieDetailResponse one(@PathVariable UUID publicId) { return movieService.findDetail(publicId); }

    @GetMapping("/{publicId}/session-info") @Operation(summary = "Entrega metadatos a Cinema Session Service")
    public MovieSessionResponse sessionInfo(@PathVariable UUID publicId) { return movieService.findSessionInfo(publicId); }

    @PostMapping @Operation(summary = "Registra una película")
    public ResponseEntity<MovieDetailResponse> create(@Valid @RequestBody MovieRequest request) {
        MovieDetailResponse response = movieService.create(request);
        return ResponseEntity.created(URI.create("/api/catalog/movies/" + response.id())).body(response);
    }
}
