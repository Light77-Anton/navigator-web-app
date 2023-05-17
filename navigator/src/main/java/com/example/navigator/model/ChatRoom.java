package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom {

    public ChatRoom(long senderId, long recipientId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    @EmbeddedId
    private ChatRoomId id;

    @Column(name = "sender_id", insertable = false, updatable = false, nullable = false)
    private long senderId;

    @Column(name = "recipient_id", insertable = false, updatable = false, nullable = false)
    private long recipientId;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages;
}
