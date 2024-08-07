package com.example.navigator.api.response;
import com.example.navigator.model.ProfessionName;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class ProfessionNamesListResponse {

    List<ProfessionName> list;
}
