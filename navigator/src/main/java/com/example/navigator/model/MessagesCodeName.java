package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "messages_code_names")
public class MessagesCodeName {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "code_name", nullable = false)
    private String codeName;

    @OneToMany(mappedBy = "messagesCodeName", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InProgramMessage> inProgramMessages;
}
