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
public class VoteRequest {

    @JsonProperty("value")
    private byte value;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("comment_content")
    private String commentContent;
}
