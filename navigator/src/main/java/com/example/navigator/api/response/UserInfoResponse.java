package com.example.navigator.api.response;
import com.example.navigator.model.*;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Data
public class UserInfoResponse {

    private Long id;
    private String socialNetworksLinks;
    private List<String> communicationLanguages;
    private String endonymInterfaceLanguage;
    private byte ranking;
    private byte currentWorkDisplay;
    private boolean isBlocked;
    private String name;
    private String email;
    private String phone;
    private LastRequest lastRequest;
    private int limitOfTheSearch;
    private boolean isMultivacancyAllowed;
    private boolean showTemporarilyInactiveEmployees;
    private boolean areLanguagesMatched;
    private EmployeeData employeeData;
    private EmployerRequests employerRequests;
    private List<ChatMessage> receivedMessages;
    private LocalDateTime regTime;
    private String role;
    private String avatar;
    private List<User> blackList;
    private List<User> favorites;
    private UserLocation userLocation;
    private int notificationCount;
    private List<Comment> commentsToUsers;
}
