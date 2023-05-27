package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class CommentId implements Serializable {

    public CommentId() {

    }

    public CommentId(long fromId, long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    @Column(name = "from_user_id", insertable = false, updatable = false, nullable = false)
    private long fromId;

    @Column(name = "to_user_id", insertable = false, updatable = false, nullable = false)
    private long toId;
}
