package com.example.navigator.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {

    @JsonProperty("interface_language")
    private String interfaceLanguage;
    @JsonProperty("communication_languages")
    private List<String> communicationLanguages;
    @JsonProperty("social_networks_links")
    private String socialNetworksLinks;
    @JsonProperty("name")
    private String name;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("is_phone_hidden")
    private boolean isPhoneHidden;
    @JsonProperty("is_email_hidden")
    private boolean isEmailHidden;
    @JsonProperty("firm_name")
    private String firmName;
    @JsonProperty("password")
    private String password;
    @JsonProperty("is_driver_license")
    private boolean isDriverLicense;
    @JsonProperty("is_auto")
    private boolean isAuto;
    @JsonProperty("are_languages_matched")
    private boolean areLanguagesMatched;
    @JsonProperty("is_multivacancy_allowed")
    private boolean isMultivacancyAllowed;
    @JsonProperty("limit_in_the_search")
    private int limit;
}
