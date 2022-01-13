package sk.tobas;

import sk.tobas.model.Album;
import sk.tobas.model.Artist;
import sk.tobas.model.Datasource;

import java.sql.SQLException;
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

        datasource.close();
    }
}
