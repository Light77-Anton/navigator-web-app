package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@Data
public class VacancyInfoResponse {

    private String professionName;
    private String jobAddress;
    private LocalDate localDate;
    private String paymentAndAdditionalInfo;
}
