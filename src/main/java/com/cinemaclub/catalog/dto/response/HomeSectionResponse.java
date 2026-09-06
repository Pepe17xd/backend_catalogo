package com.cinemaclub.catalog.dto.response;
import java.util.List;
public record HomeSectionResponse(String name, List<MovieCatalogResponse> movies) {}
