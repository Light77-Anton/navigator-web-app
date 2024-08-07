package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "employee_to_vacancy")
@Getter
@Setter
public class EmployeeToVacancy {

    public EmployeeToVacancy() {

    }

    public EmployeeToVacancy(long vacancyId, long employeeId) {
        this.employeeId = employeeId;
        this.vacancyId = vacancyId;
    }

    @EmbeddedId
    private EmployeeToVacancyId id;

    @Column(name = "employee_id", insertable = false, updatable = false, nullable = false)
    private long employeeId;

    @Column(name = "vacancy_id", insertable = false, updatable = false, nullable = false)
    private long vacancyId;
}
