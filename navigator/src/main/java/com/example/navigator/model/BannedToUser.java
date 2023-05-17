package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "banned_to_user")
@Getter
@Setter
public class BannedToUser {

    public BannedToUser() {

    }

    public BannedToUser(long bannedId, long userId) {
        this.bannedId = bannedId;
        this.userId = userId;
    }

    @EmbeddedId
    private BannedToUserId id;

    @Column(name = "banned_id", insertable = false, updatable = false, nullable = false)
    private long bannedId;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private long userId;
}