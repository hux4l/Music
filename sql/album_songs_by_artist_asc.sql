SELECT albums.name, artists.name AS artist, songs.track AS track, songs.title AS song
FROM albums
INNER JOIN artists
ON albums.artist=artists._id
INNER JOIN songs
ON albums._id = songs.album
WHERE artists.name = 'Pink Floyd'
ORDER BY albums.name ASC, songs.track ASC;