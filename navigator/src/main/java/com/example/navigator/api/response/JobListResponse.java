package com.example.navigator.api.response;
import com.example.navigator.model.Job;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class JobListResponse {

    private long jobCount;
    private List<Job> jobs;
}
