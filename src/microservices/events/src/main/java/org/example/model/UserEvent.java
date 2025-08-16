package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class UserEvent {
    private Integer userId;
    private String username;
    private String email;
    private String action;
    private LocalDateTime timestamp;

    public UserEvent() {}

    @JsonCreator
    public UserEvent(@JsonProperty("user_id") Integer userId,
                     @JsonProperty("username") String username,
                     @JsonProperty("email") String email,
                     @JsonProperty("action") String action,
                     @JsonProperty("timestamp") LocalDateTime timestamp) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.action = action;
        this.timestamp = timestamp;
    }

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
