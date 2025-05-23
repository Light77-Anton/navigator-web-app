package com.example.navigator.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyRequest {

    @JsonProperty("vacancy_id")
    private Long vacancyId;
    @JsonProperty("profession_name")
    private String professionName;
    @JsonProperty("quotas_number")
    private int quotasNumber;
    @JsonProperty("job_address")
    private String jobAddress;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("waiting_timestamp")
    private LocalDateTime waitingTimestamp;
    @JsonProperty("start_timestamp")
    private LocalDateTime startTimestamp;
    @JsonProperty("payment_and_additional_info")
    private String paymentAndAdditionalInfo;
    @JsonProperty("profession_name")
    private Long employerId;
    @JsonProperty("profession_name")
    private String employerName;
    @JsonProperty("is_required_to_close_all_quotas")
    private boolean isRequiredToCloseAllQuotas;
    @JsonProperty("save_template")
    private boolean saveTemplate;
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("employee_offer_content")
    private String employeeOfferContent;
}
