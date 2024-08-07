package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_sender_id")
    private EmployeeData employeeSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_recipient_id")
    private Company companyRecipient;

    @Column(name = "is_official_comment", nullable = false)
    private boolean isOfficialComment;

    @Column(name = "is_comment_for_user", nullable = false)
    private boolean isCommentForUser;

    @Column(name = "is_comment_for_company", nullable = false)
    private boolean isCommentForCompany;

    @Column(name = "content", nullable = false)
    private String content;
}
