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
@Table(name = "employees_data")
public class EmployeeData {

    @Id
    @Column(name = "employee_id")
    private Long id;

    @Column(name = "is_driver_license", nullable = false)
    private boolean isDriverLicense;

    @Column(name = "is_auto", nullable = false)
    private boolean isAuto;

    @Column(name = "employees_work_requirements")
    private String employeesWorkRequirements;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profession2user",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "profession_id")})
    private List<Profession> professions;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @OneToMany(mappedBy = "employeeData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobs;
}
