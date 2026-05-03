package client;

import javafx.beans.property.SimpleStringProperty;

public class MusicRecord {
    private SimpleStringProperty title;
    private SimpleStringProperty artist;
    private SimpleStringProperty genre;
    private SimpleStringProperty year;

    public MusicRecord(String title, String artist, String genre, String year) {
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.genre = new SimpleStringProperty(genre);
        this.year = new SimpleStringProperty(year);
    }

    public String getTitle() { return title.get(); }
    public String getArtist() { return artist.get(); }
    public String getGenre() { return genre.get(); }
    public String getYear() { return year.get(); }
}