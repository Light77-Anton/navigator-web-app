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
@Table(name = "employees_data")
public class EmployeeData {

    @Id
    @Column(name = "employee_id")
    private Long id;

    @Column(name = "is_driver_license", nullable = false)
    private boolean isDriverLicense;

    @Column(name = "is_auto", nullable = false)
    private boolean isAuto;

    @Column(name = "is_multivacancy_allowed", nullable = false)
    private boolean isMultivacancyAllowed;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "employee_to_vacancy",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "vacancy_id")})
    private List<Vacancy> acceptedVacancies;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "professions_to_users",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "profession_id")})
    private List<Profession> professions;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "employee_to_employer",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "employer_id")})
    private List<EmployerRequests> contactedEmployers;

    @Column(name = "status", nullable = false)
    private byte status;

    @Column(name = "active_status_start_date")
    private Long activeStatusStartDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @OneToMany(mappedBy = "employeeData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfoFromEmployee> infoFromEmployee;

    @Column(name = "default_offer_refusing_time_millis", nullable = false)
    private long defaultOfferRefusingTimeMillis;

    @OneToMany(mappedBy = "employeeSender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> commentsToCompany;
}
