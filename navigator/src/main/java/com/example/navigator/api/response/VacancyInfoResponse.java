package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Data
public class VacancyInfoResponse {

    private String professionName;
    private int quotasNumber;
    private String jobAddress;
    private double jobAddressLatitude;
    private double jobAddressLongitude;
    private LocalDateTime localDateTime;
    private LocalDateTime vacancyAvailability;
    private String paymentAndAdditionalInfo;
}
