package com.example.navigator.api.response;
import com.example.navigator.dto.TimerDTO;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class TimersListResponse {

    List<TimerDTO> list;
}
