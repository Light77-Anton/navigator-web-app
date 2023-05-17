package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "professions_to_passive_search")
public class ProfessionToPassiveSearch {

    public ProfessionToPassiveSearch(long professionId, long passiveSearchId) {
        this.professionId = professionId;
        this.passiveSearchId = passiveSearchId;
    }

    @EmbeddedId
    private ProfessionToPassiveSearchId id;

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;

    @Column(name = "passive_search_id", insertable = false, updatable = false, nullable = false)
    private long passiveSearchId;
}
