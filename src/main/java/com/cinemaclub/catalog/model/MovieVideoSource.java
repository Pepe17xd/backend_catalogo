package com.cinemaclub.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "movie_video_source",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_movie_video_quality_type", columnNames = {"movie_id", "quality", "type"}))
public class MovieVideoSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VideoQuality quality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VideoType type;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(nullable = false)
    private int priority;

    public MovieVideoSource() {
    }

    public MovieVideoSource(VideoQuality quality, VideoType type, String url, int priority) {
        this.quality = quality;
        this.type = type;
        this.url = url;
        this.priority = priority;
    }

    public Long getId() { return id; }
    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public VideoQuality getQuality() { return quality; }
    public VideoType getType() { return type; }
    public String getUrl() { return url; }
    public int getPriority() { return priority; }
}
