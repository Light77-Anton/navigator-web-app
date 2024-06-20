package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage implements Comparable<ChatMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "sender_id", insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = "recipient_id", insertable = false, updatable = false, nullable = false)
    })
    private ChatRoom chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "is_image", nullable = false)
    private boolean isImage;

    @Override
    public int compareTo(ChatMessage o) {
        if (this.time.isAfter(o.time)) {
            return 1;
        } else if(this.time.isBefore(o.time)) {
            return -1;
        } else {
            return 0;
        }
    }
}
