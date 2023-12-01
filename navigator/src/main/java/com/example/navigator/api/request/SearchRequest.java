package com.example.navigator.api.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {

    @JsonProperty("profession")
    private String professionName;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("is_auto")
    private boolean isAuto;
    @JsonProperty("are_languages_matched")
    private boolean areLanguagesMatched;
    @JsonProperty("in_radius_of")
    private int inRadiusOf;
    @JsonProperty("sort_type")
    private String SortType;
}
