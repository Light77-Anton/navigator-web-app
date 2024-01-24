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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "professions_to_users",
            joinColumns = {@JoinColumn(name = "profession_id")},
            inverseJoinColumns = {@JoinColumn(name = "employee_id")})
    private List<Profession> professions;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "active_status_start_date", nullable = false)
    private Long activeStatusStartDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @OneToMany(mappedBy = "employeeData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfoFromEmployee> infoFromEmployee;
}
