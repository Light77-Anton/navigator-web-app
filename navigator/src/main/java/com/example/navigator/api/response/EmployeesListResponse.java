package com.example.navigator.api.response;
import com.example.navigator.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class EmployeesListResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<User> employeeList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int count;
}
