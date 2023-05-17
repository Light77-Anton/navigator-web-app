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
public class InProgramMessageRequest {

    @JsonProperty("language")
    String language;
    @JsonProperty("code_name")
    String codeName;
    @JsonProperty("message")
    String message;
}
