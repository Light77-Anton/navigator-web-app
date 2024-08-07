package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class EmployeeToVacancyId {

    public EmployeeToVacancyId() {

    }

    public EmployeeToVacancyId(long vacancyId, long employeeId) {
        this.employeeId = employeeId;
        this.vacancyId = vacancyId;
    }

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;

    @Column(name = "vacancy_id", insertable = false, updatable = false, nullable = false)
    private long vacancyId;
}
