package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    public Comment(long fromId, long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    @EmbeddedId
    private CommentId id;

    @Column(name = "from_user_id", insertable = false, updatable = false, nullable = false)
    private long fromId;

    @Column(name = "to_user_id", insertable = false, updatable = false, nullable = false)
    private long toId;

    @Column(name = "content", nullable = false)
    private String content;
}
