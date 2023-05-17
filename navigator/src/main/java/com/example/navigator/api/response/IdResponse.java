package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class IdResponse {

    boolean result;
    Long id;
}
