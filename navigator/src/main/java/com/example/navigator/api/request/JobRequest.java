package com.example.navigator.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    @JsonProperty("profession_id")
    private Long professionId;
    @JsonProperty("job_address")
    private String jobAddress;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("payment_and_additional_info")
    private String paymentAndAdditionalInfo;
}
