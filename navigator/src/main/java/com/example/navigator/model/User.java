package com.example.navigator.model;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "language_to_user",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "language_id")})
    private List<Language> communicationLanguages;

    @Column(name = "social_networks_links") // посмотреть как давать ссылки в PSQL
    private String socialNetworksLinks;

    @Column(name = "interface_language", nullable = false) // не ссылается на сам Language т.к. обычно хватает только самоназвания
    private String endonymInterfaceLanguage;

    @Column(name = "ranking", nullable = false)
    private byte ranking;

    @Column(name = "is_activated", nullable = false)
    private boolean isActivated;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "is_email_hidden", nullable = false)
    private boolean isEmailHidden;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_phone_hidden", nullable = false)
    private boolean isPhoneHidden;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private LastRequest lastRequest;

    @Column(name = "notifications_count", nullable = false)
    private int notificationsCount;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> sentMessages;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> receivedMessages;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> commentsFromUser;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> commentsToUser;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votesFromUser;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votesToUser;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private EmployeeData employeeData;

    @OneToOne(mappedBy = "employer", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private EmployerRequests employerRequests;

    @Column(name = "registration_time", nullable = false)
    private LocalDateTime regTime;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "restore_code")
    private String restoreCode;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "banned_to_user",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "banned_id")})
    private List<User> blackList;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "banned_to_user",
            joinColumns = {@JoinColumn(name = "banned_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> InBlackListOf;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "favorite_to_user",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "favorite_id")})
    private List<User> favorites;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "favorite_to_user",
            joinColumns = {@JoinColumn(name = "favorite_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> favoriteOf;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserLocation userLocation;

    @Column(name = "limit_for_the_search", nullable = false)
    private int limitForTheSearch;

    @Column(name = "current_work_display", nullable = false)
    private byte currentWorkDisplay;

    @Column(name = "are_languages_matched", nullable = false)
    private boolean areLanguagesMatched;

    public String getRoleString() {
        return role;
    }

    public Role getRole() {
        if (role.equals("Employer")) {
            return  Role.EMPLOYER;
        } else if (role.equals("Employee")) {
            return  Role.EMPLOYEE;
        }

        return Role.MODERATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
