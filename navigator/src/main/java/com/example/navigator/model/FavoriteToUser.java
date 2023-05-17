package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "favorite_to_user")
@Getter
@Setter
public class FavoriteToUser {

    public FavoriteToUser() {

    }

    public FavoriteToUser(long favoriteId, long userId) {
        this.favoriteId = favoriteId;
        this.userId = userId;
    }

    @EmbeddedId
    private FavoriteToUserId id;

    @Column(name = "favorite_id", insertable = false, updatable = false, nullable = false)
    private long favoriteId;

    @Column(name = "user_id", insertable = false, updatable = false, nullable = false)
    private long userId;
}
