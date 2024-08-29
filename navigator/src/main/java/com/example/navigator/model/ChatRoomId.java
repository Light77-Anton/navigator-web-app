package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ChatRoomId implements Serializable {

    public ChatRoomId() {

    }

    public ChatRoomId(long employeeId, long employerId) {
        this.employeeId = employeeId;
        this.employerId = employerId;
    }

    @Column(name = "sender_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;

    @Column(name = "recipient_id", insertable = false, updatable = false, nullable = false)
    private long employerId;
}
