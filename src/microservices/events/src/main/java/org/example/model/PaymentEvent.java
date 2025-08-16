package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {

    @JsonProperty("payment_id")
    private Integer paymentId;

    @JsonProperty("user_id")
    private Integer userId;

    private Float amount;

    private String status;

    private LocalDateTime timestamp;

    @JsonProperty("method_type")
    private String methodType;

}
