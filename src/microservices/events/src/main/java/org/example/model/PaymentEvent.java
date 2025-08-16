package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class PaymentEvent {
    private Integer paymentId;
    private Integer userId;
    private Float amount;
    private String status;
    private LocalDateTime timestamp;
    private String methodType;

    public PaymentEvent() {}

    @JsonCreator
    public PaymentEvent(@JsonProperty("payment_id") Integer paymentId,
                        @JsonProperty("user_id") Integer userId,
                        @JsonProperty("amount") Float amount,
                        @JsonProperty("status") String status,
                        @JsonProperty("timestamp") LocalDateTime timestamp,
                        @JsonProperty("method_type") String methodType) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
        this.methodType = methodType;
    }

    @JsonProperty("payment_id")
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("method_type")
    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    @Override
    public String toString() {
        return "PaymentEvent{" +
                "paymentId=" + paymentId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", methodType='" + methodType + '\'' +
                '}';
    }
}
