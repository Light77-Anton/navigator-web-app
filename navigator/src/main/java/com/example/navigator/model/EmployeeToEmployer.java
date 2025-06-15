package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "employees_to_employers")
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

    @Column(name = "available_votes_count_from_employee", nullable = false)
    private int availableVotesCountFromEmployee;

    @Column(name = "available_votes_count_from_employer", nullable = false)
    private int availableVotesCountFromEmployer;
}
