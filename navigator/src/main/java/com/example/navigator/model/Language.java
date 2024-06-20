package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "language_endonym", nullable = false)
    private String languageEndonym;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProfessionName> professionNames;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InProgramMessage> inProgramMessages;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfoFromEmployee> infoFromEmployees;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfoAboutVacancyFromEmployer> infoAboutVacancyFromEmployers;

    @OneToMany(mappedBy = "additionalLanguage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LastRequest> additionalLanguagesOfLastRequests;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "languages_to_users",
            joinColumns = {@JoinColumn(name = "language_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> users;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "languages_to_last_request",
            joinColumns = {@JoinColumn(name = "language_id")},
            inverseJoinColumns = {@JoinColumn(name = "last_request_id")})
    private List<LastRequest> lastRequestsList;
}
