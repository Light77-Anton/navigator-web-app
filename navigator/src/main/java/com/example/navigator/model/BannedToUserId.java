package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class BannedToUserId implements Serializable {

    public BannedToUserId() {

    }

    public BannedToUserId(long bannedId, long userId) {
        this.bannedId = bannedId;
        this.userId = userId;
    }

    @Column(name = "banned_id", insertable = false, updatable = false, nullable = false)
    private long bannedId;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private long userId;
}
