package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class LanguageToUserId implements Serializable {

    public LanguageToUserId() {

    }

    public LanguageToUserId(long languageId, long userId) {
        this.languageId = languageId;
        this.userId = userId;
    }

    @Column(name = "language_id", insertable = false, updatable = false, nullable = false)
    private long languageId;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private long userId;
}
