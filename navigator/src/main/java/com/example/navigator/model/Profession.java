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
@Table(name = "professions")
public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profession_to_user",
            joinColumns = {@JoinColumn(name = "profession_id")},
            inverseJoinColumns = {@JoinColumn(name = "employee_id")})
    private List<ProfessionToUser> professionToUserList;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "professions_to_job",
            joinColumns = {@JoinColumn(name = "profession_id")},
            inverseJoinColumns = {@JoinColumn(name = "job_id")})
    private List<Job> jobs;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "professions_to_passive_search",
            joinColumns = {@JoinColumn(name = "profession_id")},
            inverseJoinColumns = {@JoinColumn(name = "passive_search_id")})
    private List<Vacancy> passiveSearches;

    @OneToMany(mappedBy = "profession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProfessionName> professionNames;
}
