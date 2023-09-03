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
public class RequestForEmployees {

    @JsonProperty("profession")
    private String professionName;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("country")
    private String country;
    @JsonProperty("city")
    private String city;
    @JsonProperty("is_auto")
    private byte isAuto;
    @JsonProperty("language")
    private String languageName;
    @JsonProperty("are_languages_match")
    private byte areLanguagesMatch;
    @JsonProperty("job_address_latitude")
    private Double jobAddressLat;
    @JsonProperty("job_address_longitude")
    private Double jobAddressLon;
}
