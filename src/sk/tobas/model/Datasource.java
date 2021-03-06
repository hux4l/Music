package sk.tobas.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {

    // DB constants
    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

    // Albums table constants
    public static final String TABLE_ALBUMS = "albums";
    public static final String COL_ALBUM_ID = "_id";
    public static final String COL_ALBUM_NAME = "name";
    public static final String COL_ALBUM_ARTIST = "artist";

    // Artists table constants
    public static final String TABLE_ARTIST = "artists";
    public static final String COL_ARTIST_ID = "_id";
    public static final String COL_ARTIST_NAME = "name";

    // Songs table constants
    public static final String TABLE_SONGS = "songs";
    public static final String COL_SONG_TRACK = "track";
    public static final String COL_SONG_TITLE = "title";
    public static final String COL_SONG_ALBUM = "album";

    // column aliases
    public static final String COL_ALIAS_SONGS = "song";

    // view names
    public static final String TABLE_ARTISTS_SONG_VIEW = "songs_artist";

    // to prevent SQL injection
    public static final String QUERY_VIEW_ARTISTS_SONG_PREP = "SELECT " + COL_SONG_TRACK + ", " + COL_ALBUM_ARTIST + ", " + COL_ALIAS_SONGS + ", " + COL_ALBUM_NAME + " FROM " + TABLE_ARTISTS_SONG_VIEW + " WHERE " + COL_ALBUM_ARTIST + " = ?";

    // prepare SQL insert statements
    public static final String INSERT_ARTIST = "INSERT INTO " + TABLE_ARTIST + "(" + COL_ARTIST_NAME + ") VALUES (?)";
    public static final String INSERT_ALBUM = "INSERT INTO " + TABLE_ALBUMS + "(" + COL_ALBUM_NAME + ", " + COL_ALBUM_ARTIST +  ") VALUES (?, ?)";
    public static final String INSERT_SONG = "INSERT INTO " + TABLE_SONGS + "(" + COL_SONG_TRACK + ", " + COL_SONG_TITLE + ", " + COL_SONG_ALBUM + ") VALUES (?, ?, ?)";

    // check if artist, album, song already exits
    public static final String QUERY_ARTIST = "SELECT " + COL_ARTIST_ID + " FROM " + TABLE_ARTIST + " WHERE " + COL_ARTIST_NAME + " = ?";
    public static final String QUERY_ALBUM = "SELECT " + COL_ALBUM_ID + " FROM " + TABLE_ALBUMS + " WHERE " + COL_ALBUM_NAME + " = ?";
    public static final String QUERY_SONG = "SELECT " + COL_SONG_TRACK + ", " + COL_SONG_TITLE + ", " + COL_SONG_ALBUM + " FROM " + TABLE_SONGS + " WHERE " + COL_SONG_TITLE + " = ?";


    private Connection conn;

    private PreparedStatement getSongsFromArtists;

    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertIntoAlbums;
    private PreparedStatement insertIntoSongs;

    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;
    private PreparedStatement querySong;

    // open sql connection
    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            // prepare statement
            getSongsFromArtists = conn.prepareStatement(QUERY_VIEW_ARTISTS_SONG_PREP);

            // to insert song
            insertIntoArtists = conn.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS);  // return keys generated by insertion
            insertIntoAlbums = conn.prepareStatement(INSERT_ALBUM, Statement.RETURN_GENERATED_KEYS);
            insertIntoSongs = conn.prepareStatement(INSERT_SONG);

            queryArtist = conn.prepareStatement(QUERY_ARTIST);
            queryAlbum = conn.prepareStatement(QUERY_ALBUM);
            querySong = conn.prepareStatement(QUERY_SONG);

            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    // close sql connection
    public void close() {
        try {
            if (getSongsFromArtists != null) {
                getSongsFromArtists.close();
            }

            // check if statements exists
            if (insertIntoArtists != null) {
                insertIntoArtists.close();
            }

            if (insertIntoAlbums != null) {
                insertIntoAlbums.close();
            }

            if (insertIntoSongs != null) {
                insertIntoSongs.close();
            }

            if (queryArtist != null) {
                queryArtist.close();
            }

            if (queryAlbum != null) {
                queryAlbum.close();
            }

            if (querySong != null) {
                querySong.close();
            }

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
    // refactored to prevent SQL injection
    public List<SongArtist> getSongsByArtist(String artist) {

        try {
            getSongsFromArtists.setString(1, artist);
            ResultSet results = getSongsFromArtists.executeQuery();

            List<SongArtist> songArtists = new ArrayList<>();
            while (results.next()) {
                SongArtist song = new SongArtist();
                song.setTrack(results.getInt(COL_SONG_TRACK));
                song.setArtistName(results.getString(COL_ALBUM_ARTIST));
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

    private int insertArtist(String artist) throws SQLException {
        queryArtist.setString(1, artist);
        ResultSet results = queryArtist.executeQuery();

        if (results.next()) {
            return results.getInt(1);
        }
        else {
            insertIntoArtists.setString(1, artist);
            // checks how many rows were affected
            int affectedRows = insertIntoArtists.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert artist");
            }

            ResultSet generatedKeys = insertIntoArtists.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Can't get _id for artist");
            }
        }
    }

    private int insertAlbum(String name, int id) throws SQLException {
        queryAlbum.setString(1, name);
        ResultSet results = queryAlbum.executeQuery();

        if (results.next()) {
            return results.getInt(1);
        }
        else {
            insertIntoAlbums.setString(1, name);
            insertIntoAlbums.setInt( 2, id);
            // checks how many rows were affected
            int affectedRows = insertIntoAlbums.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert artist");
            }

            ResultSet generatedKeys = insertIntoAlbums.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Can't get _id for artist");
            }
        }
    }

    // insert song into database
    public void insertSong(String title, String artist, String album, int track) {
        try {
            // turn off auto commit
            conn.setAutoCommit(false);

            // get artist id and album id
            int artistId = insertArtist(artist);
            int albumId = insertAlbum(album, artistId);

            // prevent duplicate song insertion
            querySong.setString(1, title);
            ResultSet results = querySong.executeQuery();
            if (results.next()) {
                if (results.getInt(COL_SONG_ALBUM) == albumId && results.getInt(COL_SONG_TRACK) == track && results.getString(COL_SONG_TITLE).equals(title)) throw new SQLException("Song already in database");
            }

            // bind values
            insertIntoSongs.setInt(1, track);
            insertIntoSongs.setString(2, title);
            insertIntoSongs.setInt(3, albumId);

            // get affected rows
            int affectedRows = insertIntoSongs.executeUpdate();
            if (affectedRows == 1) {
                // if only one affected row (added song) commit update
                conn.commit();
            } else {
                throw new SQLException("The song insert failed");
            }

        } catch (SQLException e) {
            System.out.println("Insert song exception: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                // song insertion failed, perform rollback on any changes made
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Rollback failed: " + e2.getMessage());
            }
        } finally {
            try {
                System.out.println("Resetting default behavior");
                // at the end turn autocommit back on
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Can't reset auto-commit!" + e.getMessage());
            }
        }
    }
}
