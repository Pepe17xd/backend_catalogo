-- =========================
-- GENRE
-- =========================

CREATE TABLE IF NOT EXISTS genre (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(80) NOT NULL,
    slug VARCHAR(80) NOT NULL,

    CONSTRAINT uk_genre_name UNIQUE(name),
    CONSTRAINT uk_genre_slug UNIQUE(slug)
    );


-- =========================
-- ARTIST
-- =========================

CREATE TABLE IF NOT EXISTS artist (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL,
    biography VARCHAR(4000),
    photo_url VARCHAR(2048),

    type VARCHAR(20) NOT NULL
    CHECK(type IN ('DIRECTOR','ACTOR','WRITER'))
    );


-- =========================
-- MOVIE
-- =========================

CREATE TABLE IF NOT EXISTS movie (

                                     id BIGSERIAL PRIMARY KEY,

                                     public_id UUID NOT NULL UNIQUE,

                                     title VARCHAR(255) NOT NULL,

    description VARCHAR(2000),

    release_year INTEGER,

    duration_minutes INTEGER NOT NULL,

    poster_url VARCHAR(2048),

    backdrop_url VARCHAR(2048),

    rating DOUBLE PRECISION,

    status VARCHAR(20) NOT NULL
    );


CREATE INDEX idx_movie_title
    ON movie(title);



-- =========================
-- RELACION MOVIE GENRE
-- =========================

CREATE TABLE IF NOT EXISTS movie_genre (

                                           movie_id BIGINT NOT NULL
                                           REFERENCES movie(id)
    ON DELETE CASCADE,

    genre_id BIGINT NOT NULL
    REFERENCES genre(id),

    PRIMARY KEY(movie_id,genre_id)
    );



-- =========================
-- RELACION MOVIE ARTIST
-- =========================

CREATE TABLE IF NOT EXISTS movie_artist (

                                            movie_id BIGINT NOT NULL
                                            REFERENCES movie(id)
    ON DELETE CASCADE,

    artist_id BIGINT NOT NULL
    REFERENCES artist(id),

    PRIMARY KEY(movie_id,artist_id)
    );



-- =========================
-- VIDEO SOURCES
-- =========================

CREATE TABLE IF NOT EXISTS movie_video_source (

                                                  id BIGSERIAL PRIMARY KEY,

                                                  movie_id BIGINT NOT NULL
                                                  REFERENCES movie(id)
    ON DELETE CASCADE,

    quality VARCHAR(10) NOT NULL,

    type VARCHAR(10) NOT NULL,

    url VARCHAR(2048) NOT NULL,

    priority INTEGER NOT NULL,

    CONSTRAINT uk_video_quality_type
    UNIQUE(movie_id,quality,type)
    );



-- =========================
-- SUBTITLES
-- =========================

CREATE TABLE IF NOT EXISTS movie_subtitle (

                                              id BIGSERIAL PRIMARY KEY,

                                              movie_id BIGINT NOT NULL
                                              REFERENCES movie(id)
    ON DELETE CASCADE,

    language VARCHAR(20) NOT NULL,

    url VARCHAR(2048) NOT NULL,

    CONSTRAINT uk_subtitle_language
    UNIQUE(movie_id,language)
    );



-- =========================
-- DATA INITIAL
-- =========================


INSERT INTO genre(name,slug)
VALUES

    ('Action','action'),
    ('Animation','animation'),
    ('Fantasy','fantasy'),
    ('Nature','nature')

    ON CONFLICT DO NOTHING;



INSERT INTO artist
(
    name,
    biography,
    type
)

VALUES

    (
        'Blender Foundation',
        'Organization behind several open movie projects.',
        'DIRECTOR'
    ),

    (
        'MDN Web Docs',
        'Open web documentation project.',
        'DIRECTOR'
    )

    ON CONFLICT DO NOTHING;



INSERT INTO movie
(
    public_id,
    title,
    description,
    release_year,
    duration_minutes,
    poster_url,
    backdrop_url,
    rating,
    status
)

VALUES

    (
        'a18ea230-56a9-4d37-9ef8-349e99078c53',
        'Sintel',
        'Open source fantasy short film created with Blender.',
        2010,
        15,
        'https://media.w3.org/2010/05/sintel/poster.png',
        'https://media.w3.org/2010/05/sintel/poster.png',
        6.2,
        'READY'
    ),

    (
        'bb89a5df-7571-46ed-b777-1357f98d0619',
        'Big Buck Bunny',
        'Open source animated short film created by Blender Foundation.',
        2008,
        10,
        'https://media.w3.org/2010/05/bunny/poster.png',
        'https://media.w3.org/2010/05/bunny/poster.png',
        6.1,
        'READY'
    ),

    (
        'f10e51db-b626-48e2-8967-7067a766448b',
        'Flower',
        'CC0 sample video maintained for web platform examples.',
        2020,
        1,
        NULL,
        NULL,
        6.0,
        'PROCESSING'
    )

    ON CONFLICT DO NOTHING;



-- =========================
-- MOVIE GENRES
-- =========================

INSERT INTO movie_genre(movie_id,genre_id)

SELECT
    m.id,
    g.id

FROM movie m, genre g

WHERE

    (m.title='Sintel' AND g.slug='fantasy')
   OR

    (m.title='Big Buck Bunny' AND g.slug='animation')
   OR

    (m.title='Flower' AND g.slug='nature')

    ON CONFLICT DO NOTHING;



-- =========================
-- MOVIE ARTISTS
-- =========================


INSERT INTO movie_artist(movie_id,artist_id)

SELECT
    m.id,
    a.id

FROM movie m, artist a

WHERE

    (m.title IN ('Sintel','Big Buck Bunny')
        AND a.name='Blender Foundation')

   OR

    (m.title='Flower'
        AND a.name='MDN Web Docs')

    ON CONFLICT DO NOTHING;



-- =========================
-- VIDEOS
-- =========================


INSERT INTO movie_video_source
(movie_id,quality,type,url,priority)

SELECT
    id,
    'P720',
    'MP4',
    'https://download.blender.org/durian/trailer/sintel_trailer-720p.mp4',
    10

FROM movie

WHERE title='Sintel';



INSERT INTO movie_video_source
(movie_id,quality,type,url,priority)

SELECT
    id,
    'AUTO',
    'HLS',
    'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
    10

FROM movie

WHERE title='Big Buck Bunny';



INSERT INTO movie_video_source
(movie_id,quality,type,url,priority)

SELECT
    id,
    'P720',
    'MP4',
    'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
    10

FROM movie

WHERE title='Flower';



-- =========================
-- SUBTITLES
-- =========================


INSERT INTO movie_subtitle
(movie_id,language,url)

SELECT

    id,
    'en',
    'https://interactive-examples.mdn.mozilla.net/media/examples/friday.vtt'

FROM movie

WHERE title='Flower';