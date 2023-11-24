package com.example.navigator.api.response;
import com.example.navigator.model.*;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
public class UserInfoResponse {

    private String socialNetworksLinks;
    private List<String> communicationLanguages;
    private String endonymInterfaceLanguage;
    private List<Vote> votes;
    private double ranking;
    private boolean isBlocked;
    private String name;
    private String email;
    private String phone;
    private String lastRequest;
    private int limitOfTheSearch;
    private boolean areLanguagesMatched;
    private EmployeeData employeeData;
    private EmployerRequests employerRequests;
    private LocalDateTime regTime;
    private String role;
    private String avatar;
    private List<User> blackList;
    private List<User> favorites;
    private Location location;
}
