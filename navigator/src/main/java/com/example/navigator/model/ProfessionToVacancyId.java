package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ProfessionToVacancyId implements Serializable {

    public ProfessionToVacancyId() {

    }

    public ProfessionToVacancyId(long professionId, long vacancyId) {
        this.professionId = professionId;
        this.vacancyId = vacancyId;
    }

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;

    @Column(name = "vacancy_id", insertable = false, updatable = false, nullable = false)
    private long vacancyId;
}
