package com.cinemaclub.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "movie", indexes = @Index(name = "idx_movie_title", columnList = "title"))
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;
    @NotBlank @Column(nullable = false)
    private String title;
    @Column(length = 2000)
    private String description;
    private Integer releaseYear;
    @Positive @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    @Column(length = 2048)
    private String posterUrl;
    @Column(length = 2048)
    private String backdropUrl;
    @DecimalMin("0.0") @DecimalMax("10.0")
    private Double rating;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private AvailabilityStatus status = AvailabilityStatus.READY;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new LinkedHashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_artist", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
    private Set<Artist> artists = new LinkedHashSet<>();
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieVideoSource> videoSources = new LinkedHashSet<>();
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MovieSubtitle> subtitles = new LinkedHashSet<>();

    @PrePersist void assignPublicId() { if (publicId == null) publicId = UUID.randomUUID(); }
    public Long getId() { return id; }
    public UUID getPublicId() { return publicId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getBackdropUrl() { return backdropUrl; }
    public void setBackdropUrl(String backdropUrl) { this.backdropUrl = backdropUrl; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public AvailabilityStatus getStatus() { return status; }
    public void setStatus(AvailabilityStatus status) { this.status = status; }
    public Set<Genre> getGenres() { return genres; }
    public Set<Artist> getArtists() { return artists; }
    public Set<MovieVideoSource> getVideoSources() { return videoSources; }
    public void addVideoSource(MovieVideoSource source) { videoSources.add(source); source.setMovie(this); }
    public Set<MovieSubtitle> getSubtitles() { return subtitles; }
    public void addSubtitle(MovieSubtitle subtitle) { subtitles.add(subtitle); subtitle.setMovie(this); }
}
