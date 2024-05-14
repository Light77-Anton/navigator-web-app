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
@Table(name = "employers_requests")
public class EmployerRequests {

    @Id
    @Column(name = "employer_id")
    private Long id;

    @Column(name = "firm_name")
    private String firmName;

    @OneToMany(mappedBy = "employerRequests", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vacancy> vacancies;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "employee_to_employer",
            joinColumns = {@JoinColumn(name = "employer_id")},
            inverseJoinColumns = {@JoinColumn(name = "employee_id")})
    private List<EmployeeData> contactedEmployees;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;
}
