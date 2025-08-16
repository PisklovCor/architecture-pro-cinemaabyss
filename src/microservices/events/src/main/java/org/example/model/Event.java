package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Event {
    private String id;
    private String type;
    private LocalDateTime timestamp;
    private Object payload;

    public Event() {}

    @JsonCreator
    public Event(@JsonProperty("id") String id,
                 @JsonProperty("type") String type,
                 @JsonProperty("timestamp") LocalDateTime timestamp,
                 @JsonProperty("payload") Object payload) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
