package com.example.navigator.api.response;
import com.example.navigator.model.EmployeeData;
import com.example.navigator.model.Profession;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ProfessionToUserResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Profession profession;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EmployeeData employee;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String extendedInfoFromEmployee;
}
