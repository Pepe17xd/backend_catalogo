package com.cinemaclub.catalog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "artist")
public class Artist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(length = 4000) private String biography;
    @Column(name = "photo_url", length = 2048) private String photoUrl;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private ArtistType type;
    protected Artist() {}
    public Artist(String name, String biography, String photoUrl, ArtistType type) { this.name = name; this.biography = biography; this.photoUrl = photoUrl; this.type = type; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBiography() { return biography; }
    public String getPhotoUrl() { return photoUrl; }
    public ArtistType getType() { return type; }
}
