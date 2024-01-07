package com.example.navigator.api.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.HashMap;

@Component
@Data
public class ExtendedUserInfoResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double rating;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firmName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String avatar;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String employeesWorkRequirements;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean isDriverLicense;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean isAuto;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HashMap<String, String> ProfessionsAndInfoAboutIt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

}
