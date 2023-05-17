package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ProfessionToUserId implements Serializable {

    public ProfessionToUserId() {

    }

    public ProfessionToUserId(long professionId, long employeeId) {
        this.professionId = professionId;
        this.employeeId = employeeId;
    }

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;
}
