package com.example.navigator.api.response;
import com.example.navigator.model.Vacancy;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class VacanciesList {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Vacancy> employeeList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
}
