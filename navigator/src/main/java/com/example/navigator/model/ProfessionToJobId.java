package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ProfessionToJobId implements Serializable {

    public ProfessionToJobId() {

    }

    public ProfessionToJobId(long professionId, long jobId) {
        this.professionId = professionId;
        this.jobId = jobId;
    }

    @Column(name = "job_id", insertable = false, updatable = false, nullable = false)
    private long jobId;

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;
}
