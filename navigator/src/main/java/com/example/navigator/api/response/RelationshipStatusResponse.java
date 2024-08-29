package com.example.navigator.api.response;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RelationshipStatusResponse {

    private boolean isFavorite;
    private boolean isInBlackList;
}
