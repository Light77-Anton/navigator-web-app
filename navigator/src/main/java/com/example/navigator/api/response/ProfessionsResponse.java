package com.example.navigator.api.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class ProfessionsResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> list;
}
