package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    @JsonProperty("user_id")
    private Integer userId;

    private String username;

    private String email;

    private String action;

    private LocalDateTime timestamp;

}
