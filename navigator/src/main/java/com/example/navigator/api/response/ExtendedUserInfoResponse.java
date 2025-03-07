package com.example.navigator.api.response;
import com.example.navigator.model.Vote;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class ExtendedUserInfoResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private byte rating;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
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
    private String infoFromEmployee;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String languages;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean isDriverLicense;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean isAuto;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String professions;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String socialNetworksLinks;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Vote> votesToUser;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

}
