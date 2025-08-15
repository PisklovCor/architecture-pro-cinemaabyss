package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Movie {
    private String id;
    private String title;
    private String genre;
    private Integer year;

    public Movie() {}

    @JsonCreator
    public Movie(@JsonProperty("id") String id,
                 @JsonProperty("title") String title,
                 @JsonProperty("genre") String genre,
                 @JsonProperty("year") Integer year) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                '}';
    }
}
