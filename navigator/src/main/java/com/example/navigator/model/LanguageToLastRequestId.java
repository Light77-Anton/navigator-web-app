package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class LanguageToLastRequestId {

    public LanguageToLastRequestId() {

    }

    public LanguageToLastRequestId(long languageId, long lastRequestId) {
        this.languageId = languageId;
        this.lastRequestId = lastRequestId;
    }

    @Column(name = "language_id", insertable = false, updatable = false, nullable = false)
    private long languageId;

    @Column(name = "last_request_id", insertable = false, updatable = false, nullable = false)
    private long lastRequestId;
}
