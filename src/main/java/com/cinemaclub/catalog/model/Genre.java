package com.cinemaclub.catalog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "genre", uniqueConstraints = {@UniqueConstraint(name = "uk_genre_name", columnNames = "name"), @UniqueConstraint(name = "uk_genre_slug", columnNames = "slug")})
public class Genre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 80) private String name;
    @Column(nullable = false, length = 80) private String slug;
    protected Genre() {}
    public Genre(String name, String slug) { this.name = name; this.slug = slug; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
}
