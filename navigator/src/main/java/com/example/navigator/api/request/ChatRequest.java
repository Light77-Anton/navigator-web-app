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
public class ChatRequest {

    @JsonProperty("sender_id")
    private Long senderId;
    @JsonProperty("recipient_id")
    private Long recipientId;
    @JsonProperty("content")
    private String content;
    @JsonProperty("is_image")
    private boolean isImage;
}
