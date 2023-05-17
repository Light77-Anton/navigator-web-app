package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ProfessionToPassiveSearchId implements Serializable {

    public ProfessionToPassiveSearchId() {

    }

    public ProfessionToPassiveSearchId(long professionId, long passiveSearchId) {
        this.professionId = professionId;
        this.passiveSearchId = passiveSearchId;
    }

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;

    @Column(name = "passive_search_id", insertable = false, updatable = false, nullable = false)
    private long passiveSearchId;
}
