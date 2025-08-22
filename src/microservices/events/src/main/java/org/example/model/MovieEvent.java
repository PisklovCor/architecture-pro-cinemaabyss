package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieEvent {

    @JsonProperty("movie_id")
    private Integer movieId;

    private String title;

    private String action;

    @JsonProperty("user_id")
    private Integer userId;

    private Float rating;

    private List<String> genres;

    private String description;

}
