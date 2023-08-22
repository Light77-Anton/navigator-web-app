package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@Data
public class MapTextResponse {

    private Map<String, String> map;
}
