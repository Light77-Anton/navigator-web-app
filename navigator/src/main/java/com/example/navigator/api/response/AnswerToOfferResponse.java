package com.example.navigator.api.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AnswerToOfferResponse {

    private byte decision;
    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long recipientId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
}
