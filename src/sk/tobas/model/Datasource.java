package sk.tobas.model;

import javax.swing.plaf.nimbus.State;
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
    public static final String COL_SONG_TRACK = "track";
    public static final String COL_SONG_TITLE = "title";
    public static final String COL_SONG_ALBUM = "album";

    public static final String COL_ALIAS_SONGS = "song";

    public static final String TABLE_ARTISTS_SONG_VIEW = "songs_artist";
    // view can be created here as well

    private Connection conn;

    // open sql connection
    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    // close sql connection
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

    // get albums from artis passed as parameter
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

    // get songs by artist an album
    public List<SongArtist> getSongsByArtistAlbum(String name) {
        try (Statement statement = conn.createStatement();
             ResultSet results = statement.executeQuery("SELECT " + TABLE_ARTIST + "." + COL_ARTIST_NAME + " AS artist, " + TABLE_ALBUMS + "." + COL_ALBUM_NAME + ", " + TABLE_SONGS + "." + COL_SONG_TRACK + " AS track, " + TABLE_SONGS + "." + COL_SONG_TITLE + " AS song " +
                     "FROM " + TABLE_ALBUMS +
                     " INNER JOIN " + TABLE_ARTIST +
                     " ON " + TABLE_ALBUMS + "." + COL_ALBUM_ARTIST + "=" + TABLE_ARTIST + "." + COL_ARTIST_ID +
                     " INNER JOIN " + TABLE_SONGS +
                     " ON " + TABLE_ALBUMS + "." + COL_ALBUM_ID + " = " + TABLE_SONGS + "." + COL_SONG_ALBUM +
                     " WHERE " + TABLE_ARTIST +"." + COL_ARTIST_NAME + " = '" + name + "'" +
                     " ORDER BY " + TABLE_ALBUMS + "." + COL_ALBUM_NAME + " ASC, " + TABLE_SONGS + "." + COL_SONG_TRACK + " ASC");) {

            List<SongArtist> songs = new ArrayList<>();
            while(results.next()) {
                SongArtist songArtist = new SongArtist();
                songArtist.setArtistName(results.getString("artist"));
                songArtist.setAlbumName(results.getString("name"));
                songArtist.setTrack(results.getInt("track"));
                songArtist.setSongTitle(results.getString("song"));

                songs.add(songArtist);
            }
            return songs;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    // get row count from table name from parameter
    public int getCount(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sql)) {
            return results.getInt(1);
        } catch (SQLException e) {
            System.out.println("Table not found: " + e.getMessage());
            return -1;
        }
    }

    // get metadata from songs table
    public void querySongsMetadata() {
        String sql = "SELECT * FROM " + TABLE_SONGS;

        try (Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql)) {

            ResultSetMetaData meta = results.getMetaData();
            int numColumns = meta.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                System.out.format("Column %d in the songs table is names %s\n",
                        i, meta.getColumnName(i));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // get track number, album name artist and song names from artist based on name from SQL view
    public List<SongArtist> getSongsByArtist(String artist) {
        StringBuilder sb = new StringBuilder("SELECT " + COL_SONG_TRACK + ", " + COL_ALBUM_ARTIST + ", " + COL_ALIAS_SONGS + ", " + COL_ALBUM_NAME + " FROM " + TABLE_ARTISTS_SONG_VIEW + " WHERE " + COL_ALBUM_ARTIST + "=\"" + artist + "\"");
        try (Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sb.toString())) {

            List<SongArtist> songArtists = new ArrayList<>();
            while (results.next()) {
                SongArtist song = new SongArtist();
                song.setTrack(results.getInt(COL_SONG_TRACK));
                song.setArtistName(results.getString(COL_ALBUM_NAME));
                song.setSongTitle(results.getString(COL_ALIAS_SONGS));
                song.setAlbumName(results.getString(COL_ALBUM_NAME));

                songArtists.add(song);
            }

            return songArtists;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
