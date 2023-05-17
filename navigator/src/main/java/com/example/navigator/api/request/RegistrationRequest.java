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
public class RegistrationRequest {

    @JsonProperty("interface_language")
    private String interfaceLanguage;
    @JsonProperty("communication_languages")
    private List<String> communicationLanguages;
    @JsonProperty("role")
    private String role;
    @JsonProperty("social_networks_links")
    private String socialNetworksLinks;
    @JsonProperty("is_driver_license")
    private boolean isDriverLicense;
    @JsonProperty("is_auto")
    private boolean isAuto;
    @JsonProperty("professions")
    private List<String> professions;
    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("password")
    private String password;
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("country")
    private String country;
    @JsonProperty("city")
    private String city;
    @JsonProperty("code")
    private String code;
    @JsonProperty("secret_code")
    private String secretCode;
}
