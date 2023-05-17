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
public class ProfessionRequest {

    @JsonProperty("profession")
    private String profession;
    @JsonProperty("language")
    private String language;
    @JsonProperty("profession_id")
    private Long professionId;
}
