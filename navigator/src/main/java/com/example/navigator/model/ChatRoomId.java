package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ChatRoomId implements Serializable {

    public ChatRoomId() {

    }

    public ChatRoomId(long senderId, long recipientId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    @Column(name = "sender_id", insertable = false, updatable = false, nullable = false)
    private long senderId;

    @Column(name = "recipient_id", insertable = false, updatable = false, nullable = false)
    private long recipientId;
}
