package sk.tobas;

import sk.tobas.model.Album;
import sk.tobas.model.Artist;
import sk.tobas.model.Datasource;
import sk.tobas.model.SongArtist;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Datasource datasource = new Datasource();
        if (!datasource.open()) {
            System.out.println("Can't open datasource");
            return;
        }

        List<Artist> artists = datasource.queryArtists();
        if (artists == null) {
            System.out.println("No artists!");
            return;
        }

        for (Artist artist : artists) {
            System.out.println(artist.toString());
        }

        List<Album> albums = datasource.getAlbumByArtist("Queen");
        if (albums == null) {
            System.out.println("No albums for artist found");
            return;
        }

        for (Album album: albums) {
            System.out.println(album.toString());
        }

        List<SongArtist> songs = datasource.getSongsByArtist("Queen");
        if (songs == null) {
            System.out.println("No songs for artist!");
            return;
        }

        for (SongArtist songArtist: songs) {
            if(songArtist.getTrack() == 1) System.out.println(songArtist.getArtistName() + " : " + songArtist.getAlbumName());
            System.out.println(songArtist.toString());
        }

        int rowsCount = datasource.getCount("artists");
        if (rowsCount == -1) {
            System.out.println("No records found");
        } else {
            System.out.println("Table has " + rowsCount + " rows");
        }

        datasource.querySongsMetadata();

        datasource.close();
    }
}
