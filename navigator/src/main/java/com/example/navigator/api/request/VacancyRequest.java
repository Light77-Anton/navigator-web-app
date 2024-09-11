package com.example.navigator.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyRequest {

    @JsonProperty("vacancy_id")
    private Long vacancyId;
    @JsonProperty("profession_name")
    private String professionName;
    @JsonProperty("job_address")
    private String jobAddress;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("waiting_timestamp")
    private LocalDateTime waitingTimestamp;
    @JsonProperty("start_timestamp")
    private LocalDateTime startTimestamp;
    @JsonProperty("payment_and_additional_info")
    private String paymentAndAdditionalInfo;
}
