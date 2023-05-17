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
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("password")
    private String password;
    @JsonProperty("professions_and_extended_info")
    private List<String> professionsAndExtendedInfo;
    @JsonProperty("status")
    private String status;
    @JsonProperty("special_equipment")
    private String specialEquipment;
    @JsonProperty("employees_work_requirements")
    private String employeesWorkRequirements;
    @JsonProperty("is_driver_license")
    private boolean isDriverLicense;
    @JsonProperty("is_auto")
    private boolean isAuto;
    @JsonProperty("start_activity_date_timestamp")
    private Long startActivityDateTimestamp;
}
