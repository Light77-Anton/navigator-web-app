package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(name = "is_initial_comment", nullable = false)
    private boolean isInitialComment;

    @Column(name = "is_response_to_another_comment", nullable = false)
    private boolean isResponseForAnotherComment;

    @OneToMany(mappedBy = "initialComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_comment_id")
    private Comment initialComment;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @OneToOne
    @MapsId
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;
}
