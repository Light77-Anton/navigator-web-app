package com.example.navigator.model;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "social_networks_links") // посмотреть как давать ссылки в PSQL
    private String socialNetworksLinks;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "language_to_user",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "language_id")})
    private List<Language> communicationLanguages;

    @Column(name = "interface_language", nullable = false)
    private String endonymInterfaceLanguage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;

    @Column(name = "ranking", nullable = false)
    private double ranking;

    @Column(name = "is_activated", nullable = false)
    private boolean isActivated;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "is_email_hidden", nullable = false)
    private boolean isEmailHidden;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_phone_hidden", nullable = false)
    private boolean isPhoneHidden;

    @Column(name = "last_request")
    private String lastRequest;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatNotification> notifications;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> sentMessages;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> receivedMessages;

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
    private Location location;

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


    /*
    public Role getRole() {

        return switch (role) {
            case "Employer" -> Role.EMPLOYER;
            case "Employee" -> Role.EMPLOYEE;
            default -> Role.MODERATOR;
        };
    }

     */
}
