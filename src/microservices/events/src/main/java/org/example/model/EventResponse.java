package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EventResponse {
    private String status;
    private Integer partition;
    private Long offset;
    private Event event;

    public EventResponse() {}

    @JsonCreator
    public EventResponse(@JsonProperty("status") String status,
                         @JsonProperty("partition") Integer partition,
                         @JsonProperty("offset") Long offset,
                         @JsonProperty("event") Event event) {
        this.status = status;
        this.partition = partition;
        this.offset = offset;
        this.event = event;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "EventResponse{" +
                "status='" + status + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", event=" + event +
                '}';
    }
}
