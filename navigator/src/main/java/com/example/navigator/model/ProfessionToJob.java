package com.example.navigator.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "professions_to_job")
@Getter
@Setter
public class ProfessionToJob {

    public ProfessionToJob() {

    }

    public ProfessionToJob(long professionId, long jobId) {
        this.professionId = professionId;
        this.jobId = jobId;
    }

    @EmbeddedId
    private ProfessionToJobId id;

    @Column(name = "job_id", insertable = false, updatable = false, nullable = false)
    private long jobId;

    @Column(name = "profession_id", insertable = false, updatable = false, nullable = false)
    private long professionId;
}
