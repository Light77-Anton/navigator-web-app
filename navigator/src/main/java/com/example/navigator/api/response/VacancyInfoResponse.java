package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Data
public class VacancyInfoResponse {

    private String professionName;
    private String jobAddress;
    private LocalDateTime localDateTime;
    private LocalDateTime vacancyAvailability;
    private String paymentAndAdditionalInfo;
}
