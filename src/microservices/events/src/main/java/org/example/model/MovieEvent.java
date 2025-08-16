package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MovieEvent {
    private Integer movieId;
    private String title;
    private String action;
    private Integer userId;
    private Float rating;
    private List<String> genres;
    private String description;

    public MovieEvent() {}

    @JsonCreator
    public MovieEvent(@JsonProperty("movie_id") Integer movieId,
                      @JsonProperty("title") String title,
                      @JsonProperty("action") String action,
                      @JsonProperty("user_id") Integer userId,
                      @JsonProperty("rating") Float rating,
                      @JsonProperty("genres") List<String> genres,
                      @JsonProperty("description") String description) {
        this.movieId = movieId;
        this.title = title;
        this.action = action;
        this.userId = userId;
        this.rating = rating;
        this.genres = genres;
        this.description = description;
    }

    @JsonProperty("movie_id")
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MovieEvent{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", action='" + action + '\'' +
                ", userId=" + userId +
                ", rating=" + rating +
                ", genres=" + genres +
                ", description='" + description + '\'' +
                '}';
    }
}
