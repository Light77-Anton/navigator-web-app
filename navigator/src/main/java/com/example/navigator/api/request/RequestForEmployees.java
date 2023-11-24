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
public class RequestForEmployees {

    @JsonProperty("profession")
    private String professionName;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("is_auto")
    private boolean isAuto;
    @JsonProperty("are_languages_matched")
    private boolean areLanguagesMatched;
    @JsonProperty("language")
    private List<String> communicationLanguages;
    @JsonProperty("in_radius_of")
    private int inRadiusOf;
    @JsonProperty("job_address_latitude")
    private Double jobAddressLat;
    @JsonProperty("job_address_longitude")
    private Double jobAddressLon;
}
