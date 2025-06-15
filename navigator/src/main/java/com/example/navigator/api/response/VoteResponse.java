package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VoteResponse {

    private byte value;
    private int averageValue;
    private long userId;
    private boolean isResult;
    private String error;
}
