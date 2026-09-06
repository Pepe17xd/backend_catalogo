package com.cinemaclub.catalog.controller;

import com.cinemaclub.catalog.dto.response.*;
import com.cinemaclub.catalog.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Catalog", description = "Home y búsqueda transversal del catálogo")
public class CatalogController {
    private final MovieService movieService;
    public CatalogController(MovieService movieService) { this.movieService = movieService; }
    @GetMapping("/home") @Operation(summary = "Contenido para una home tipo Netflix")
    public HomeResponse home() { return movieService.home(); }
    @GetMapping("/search") @Operation(summary = "Busca por título, género o artista")
    public List<MovieCatalogResponse> search(@RequestParam("q") String query) { return movieService.search(query); }
}
