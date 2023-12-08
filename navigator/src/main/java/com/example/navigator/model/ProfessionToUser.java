package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "profession_to_user")
@Getter
@Setter
public class ProfessionToUser {

    public ProfessionToUser() {

    }

    public ProfessionToUser(Profession profession, EmployeeData employee) {
        this.profession = profession;
        this.employee = employee;
    }

    @EmbeddedId
    private ProfessionToUserId id;

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private Profession profession;

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private EmployeeData employee;

    @Column(name = "extended_info_from_employee")
    private String extendedInfoFromEmployee;
}
