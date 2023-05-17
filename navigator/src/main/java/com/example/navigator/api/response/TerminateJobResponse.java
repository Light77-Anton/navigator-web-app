package com.example.navigator.api.response;
import com.example.navigator.model.Job;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TerminateJobResponse {

    private Job job;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private String recipientAnswer;
    private String recipientName;
}
