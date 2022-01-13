package sk.tobas.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {

    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

    public static final String TABLE_ALBUMS = "albums";
    public static final String COL_ALBUM_ID = "_id";
    public static final String COL_ALBUM_NAME = "name";
    public static final String COL_ALBUM_ARTIST = "artist";

    public static final String TABLE_ARTIST = "artists";
    public static final String COL_ARTIST_ID = "_id";
    public static final String COL_ARTIST_NAME = "name";

    public static final String TABLE_SONGS = "songs";
    public static final String COL_SONG_TRACT = "track";
    public static final String COL_SONG_TITLE = "title";
    public static final String COL_SONG_ALBUM = "album";

    private Connection conn;

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if(conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public List<Artist> queryArtists() {

        try (Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM " + TABLE_ARTIST)) {

            List<Artist> artists = new ArrayList<>();
            while (results.next()) {
                Artist artist = new Artist();
                artist.setId(results.getInt(COL_ARTIST_ID));
                artist.setName(results.getString(COL_ARTIST_NAME));
                artists.add(artist);
            }
            return artists;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public List<Album> getAlbumByArtist(String name) {
        try (Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery("SELECT albums.name, artists.name AS artist " +
                    "FROM albums " +
                    "INNER JOIN artists " +
                    "ON albums.artist=artists._id " +
                    "WHERE artists.name = '" + name + "'")) {

            List<Album> albums = new ArrayList<>();
            while (results.next()) {
                Album album = new Album();
                album.setName(results.getString("name"));
                album.setArtistName(results.getString("artist"));
                albums.add(album);
            }
            return albums;

        } catch (SQLException e) {
            System.out.println("Can't find any artist: " + e.getMessage());
            return null;
        }
    }
}
