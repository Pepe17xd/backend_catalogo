package com.cinemaclub.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "movie_subtitle",
        uniqueConstraints = @UniqueConstraint(name = "uk_movie_subtitle_language", columnNames = {"movie_id", "language"}))
public class MovieSubtitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false, length = 20)
    private String language;

    @Column(nullable = false, length = 2048)
    private String url;

    public MovieSubtitle() {
    }

    public MovieSubtitle(String language, String url) {
        this.language = language;
        this.url = url;
    }

    public Long getId() { return id; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public String getLanguage() { return language; }
    public String getUrl() { return url; }
}
