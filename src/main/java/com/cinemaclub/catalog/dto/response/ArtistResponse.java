package com.cinemaclub.catalog.dto.response;
import com.cinemaclub.catalog.model.ArtistType;
public record ArtistResponse(String name, String biography, String photoUrl, ArtistType type) {}
