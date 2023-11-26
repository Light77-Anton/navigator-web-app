package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "job_locations")
public class JobLocation {

    @Id
    @Column(name = "vacancy_id")
    private Long id;

    @Column(name = "job_address", nullable = false)
    private String jobAddress;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @OneToOne
    @MapsId
    @JoinColumn(name = "vacancy_id", nullable = false)
    private Vacancy vacancy;
}
