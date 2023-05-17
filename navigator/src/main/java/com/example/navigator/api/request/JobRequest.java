package com.example.navigator.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    @JsonProperty("id")
    private Long userId;
    @JsonProperty("professions")
    private List<String> professions;
    @JsonProperty("job_address")
    private String jobAddress;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("lowest_border_timestamp")
    private Long lowestBorderTimestamp;
    @JsonProperty("highest_border_timestamp")
    private Long highestBorderTimestamp;
    @JsonProperty("payment_and_additional_info")
    private String paymentAndAdditionalInfo;
    @JsonProperty("expiration_time")
    private Long expirationTime;
}
