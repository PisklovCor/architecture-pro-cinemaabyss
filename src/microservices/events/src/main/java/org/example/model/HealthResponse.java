package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthResponse {
    private Boolean status;

    public HealthResponse() {}

    @JsonCreator
    public HealthResponse(@JsonProperty("status") Boolean status) {
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HealthResponse{" +
                "status=" + status +
                '}';
    }
}
