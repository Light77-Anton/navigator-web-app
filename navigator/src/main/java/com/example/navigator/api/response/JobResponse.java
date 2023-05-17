package com.example.navigator.api.response;

import com.example.navigator.model.Job;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class JobResponse {

    private boolean result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Job job;
}
