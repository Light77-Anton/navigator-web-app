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
public class ProfessionToUserRequest {

    @JsonProperty("profession_id")
    private long professionId;
    @JsonProperty("additional_info")
    private String additionalInfo;
}
