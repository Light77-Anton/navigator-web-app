package com.example.navigator.api.response;
import com.example.navigator.model.Vacancy;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class VacancyListResponse {

    private List<Vacancy> list;
}
