package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "profession_to_vacancy")
public class ProfessionToVacancy {

    public ProfessionToVacancy(long professionId, long vacancyId) {
        this.professionId = professionId;
        this.vacancyId = vacancyId;
    }

    @EmbeddedId
    private ProfessionToVacancyId id;

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;

    @Column(name = "vacancy_id", insertable = false, updatable = false, nullable = false)
    private long vacancyId;
}
