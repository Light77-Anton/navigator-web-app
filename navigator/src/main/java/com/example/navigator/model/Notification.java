package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "job_address", nullable = false)
    private String jobAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profession_name_id", nullable = false)
    private ProfessionName professionName;

    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @JoinColumn(name = "opposite_user_id", nullable = false)
    private User oppositeUser;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;
}
