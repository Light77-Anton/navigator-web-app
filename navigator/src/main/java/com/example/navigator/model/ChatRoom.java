package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom {

    public ChatRoom(long employeeId, long employerId) {
        this.employeeId = employeeId;
        this.employerId = employerId;
    }

    @EmbeddedId
    private ChatRoomId id;

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;

    @Column(name = "employer_id", insertable = false, updatable = false, nullable = false)
    private long employerId;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages;
}
