package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "employee_to_employer")
@Getter
@Setter
public class EmployeeToEmployer {

    public EmployeeToEmployer() {

    }

    public EmployeeToEmployer(long employeeId, long employerId) {
        this.employeeId = employeeId;
        this.employerId = employerId;
    }

    @EmbeddedId
    private EmployeeToEmployerId id;

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;

    @Column(name = "employer_id", insertable = false, updatable = false, nullable = false)
    private long employerId;
}
