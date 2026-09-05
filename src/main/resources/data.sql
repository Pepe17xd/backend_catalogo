-- The catalog stores references only. Binary video and image files remain in external storage.
INSERT INTO movie
    (title, genre, director, release_year, description, rating, video_url, poster_url, backdrop_url,
     duration, active, availability_status)
VALUES
    ('Sintel', 'Fantasy', 'Blender Foundation', 2010,
     'Open source fantasy short film created with Blender.', 6.2,
     'https://download.blender.org/durian/trailer/sintel_trailer-720p.mp4',
     'https://media.w3.org/2010/05/sintel/poster.png',
     'https://media.w3.org/2010/05/sintel/poster.png', 15, true, 'READY'),
    ('Big Buck Bunny', 'Animation', 'Blender Foundation', 2008,
     'Open source animated short film created by the Blender Foundation.', 6.1,
     'https://media.w3.org/2010/05/bunny/trailer.mp4',
     'https://media.w3.org/2010/05/bunny/poster.png',
     'https://media.w3.org/2010/05/bunny/poster.png', 10, true, 'READY'),
    ('Flower', 'Nature', 'MDN Web Docs', 2020,
     'CC0 sample video maintained for web platform examples.', 6.0,
     'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
     null, null, 1, true, 'PROCESSING');

-- Optional renditions for adaptive/manual quality selection. video_url remains the backward-compatible default.
INSERT INTO movie_video_source (movie_id, quality, type, url, priority)
SELECT id, 'P720', 'MP4', 'https://download.blender.org/durian/trailer/sintel_trailer-720p.mp4', 20
FROM movie WHERE title = 'Sintel';

INSERT INTO movie_video_source (movie_id, quality, type, url, priority)
SELECT id, 'P1080', 'MP4', 'https://download.blender.org/durian/trailer/sintel_trailer-1080p.mp4', 30
FROM movie WHERE title = 'Sintel';

INSERT INTO movie_video_source (movie_id, quality, type, url, priority)
SELECT id, 'AUTO', 'HLS', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8', 10
FROM movie WHERE title = 'Big Buck Bunny';

INSERT INTO movie_video_source (movie_id, quality, type, url, priority)
SELECT id, 'AUTO', 'DASH', 'https://dash.akamaized.net/akamai/bbb_30fps/bbb_30fps.mpd', 20
FROM movie WHERE title = 'Big Buck Bunny';

INSERT INTO movie_subtitle (movie_id, language, url)
SELECT id, 'en', 'https://interactive-examples.mdn.mozilla.net/media/examples/friday.vtt'
FROM movie WHERE title = 'Flower';
