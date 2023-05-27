package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class FavoriteToUserId implements Serializable {

    public FavoriteToUserId() {

    }

    public FavoriteToUserId(long favoriteId, long userId) {
        this.favoriteId = favoriteId;
        this.userId = userId;
    }

    @Column(name = "favorite_id", insertable = false, updatable = false, nullable = false)
    private long favoriteId;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private long userId;
}
