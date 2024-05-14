package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class EmployeeToEmployerId implements Serializable {

    public EmployeeToEmployerId() {

    }

    public EmployeeToEmployerId(long employeeId, long employerId) {
        this.employeeId = employeeId;
        this.employerId = employerId;
    }

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;

    @Column(name = "employer_id", insertable = false, updatable = false, nullable = false)
    private long employerId;
}
