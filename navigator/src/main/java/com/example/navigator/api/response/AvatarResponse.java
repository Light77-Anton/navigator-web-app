package com.example.navigator.api.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class AvatarResponse {

    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String pathToFile;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;
}
