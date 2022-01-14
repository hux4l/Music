SELECT albums.name, artists.name AS artist
FROM albums
INNER JOIN artists
ON albums.artist=artists._id
WHERE artists.name = 'Genesis';