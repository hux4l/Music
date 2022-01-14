package sk.tobas.model;

public class SongArtist {

    private String artistName;
    private String albumName;
    private int track;
    private String songTitle;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artisName) {
        this.artistName = artisName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    @Override
    public String toString() {
        return this.track + ". " + this.artistName + " - " + this.songTitle + " (" + this.albumName + ")";
    }
}
