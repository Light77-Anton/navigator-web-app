package com.example.navigator.api.request;
import com.example.navigator.model.Language;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.HashMap;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfoForEmployersRequest {

    @JsonProperty("map")
    HashMap<Language, String> map;
}
