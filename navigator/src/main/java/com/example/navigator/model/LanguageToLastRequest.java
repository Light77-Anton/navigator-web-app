package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "languages_to_last_request")
@Getter
@Setter
public class LanguageToLastRequest {

    public LanguageToLastRequest() {

    }

    public LanguageToLastRequest(long languageId, long lastRequestId) {
        this.languageId = languageId;
        this.lastRequestId = lastRequestId;
    }

    @EmbeddedId
    private LanguageToLastRequestId id;

    @Column(name = "language_id", insertable = false, updatable = false, nullable = false)
    private long languageId;

    @Column(name = "last_request_id", insertable = false, updatable = false, nullable = false)
    private long lastRequestId;
}
