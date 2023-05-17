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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "language2user",
            joinColumns = {@JoinColumn(name = "language_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> users;
}
