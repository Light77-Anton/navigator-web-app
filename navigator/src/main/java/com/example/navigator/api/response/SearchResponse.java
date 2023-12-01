package com.example.navigator.api.response;
import com.example.navigator.model.SavedRequest;
import com.example.navigator.model.User;
import com.example.navigator.model.Vacancy;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class SearchResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<User> employeeList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Vacancy> vacancyList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<SavedRequest> savedRequestsList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int count;
}
