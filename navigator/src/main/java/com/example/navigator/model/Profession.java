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
    @JoinTable(name = "professions_to_users",
            joinColumns = {@JoinColumn(name = "profession_id")},
            inverseJoinColumns = {@JoinColumn(name = "employee_id")})
    private List<EmployeeData> employeeDataList;

    @OneToMany(mappedBy = "profession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vacancy> vacancies;

    @OneToMany(mappedBy = "profession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProfessionName> professionNames;

    @OneToMany(mappedBy = "profession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LastRequest> lastRequests;

    @Column(name = "default_offer_refusing_time_millis", nullable = false)
    private long defaultOfferRefusingTimeMillis;
}
