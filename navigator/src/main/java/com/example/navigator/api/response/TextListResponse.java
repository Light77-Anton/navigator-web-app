package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class TextListResponse {

    private List<String> list;
}
