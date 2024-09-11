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
public class DecisionRequest {

    @JsonProperty("decision")
    private String decision;
    @JsonProperty("recipient_id")
    private String recipientId;
    @JsonProperty("vacancy_id")
    private String vacancyId;
}
