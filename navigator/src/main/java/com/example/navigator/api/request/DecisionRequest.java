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
    @JsonProperty("net_confirmed_job_id")
    private Long notConfirmedJobId;
    @JsonProperty("passive_search_id")
    private Long passiveSearchId;
}
