package com.example.navigator.api.response;
import com.example.navigator.model.ChatMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class ChatMessageResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ChatMessage> messages;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ChatMessage message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long messageCount;

}
