package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "profession_to_user")
@Getter
@Setter
public class ProfessionToUser {

    public ProfessionToUser() {

    }

    public ProfessionToUser(long professionId, long employeeId) {
        this.professionId = professionId;
        this.employeeId = employeeId;
    }

    @EmbeddedId
    private ProfessionToUserId id;

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;

    @Column(name = "extended_info_from_employee")
    private String extendedInfoFromEmployee;
}
