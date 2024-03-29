package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "languages_to_users")
@Getter
@Setter
public class LanguageToUser {

    public LanguageToUser() {

    }

    public LanguageToUser(long languageId, long userId) {
        this.languageId = languageId;
        this.userId = userId;
    }

    @EmbeddedId
    private LanguageToUserId id;

    @Column(name = "language_id", insertable = false, updatable = false, nullable = false)
    private long languageId;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private long userId;
}
