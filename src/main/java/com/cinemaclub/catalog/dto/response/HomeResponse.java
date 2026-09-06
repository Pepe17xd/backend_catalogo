package com.cinemaclub.catalog.dto.response;
import java.util.List;
public record HomeResponse(MovieCatalogResponse featuredMovie, List<HomeSectionResponse> sections) {}
