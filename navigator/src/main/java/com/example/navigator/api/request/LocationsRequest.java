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
public class LocationsRequest {

    @JsonProperty("latitude_one")
    private double lat1;
    @JsonProperty("longitude_one")
    private double long1;
    @JsonProperty("latitude_two")
    private double lat2;
    @JsonProperty("longitude_two")
    private double long2;
}
