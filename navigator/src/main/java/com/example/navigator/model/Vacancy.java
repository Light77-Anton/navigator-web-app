package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vacancies")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacancies", nullable = false)
    private Profession profession;

    @OneToOne(mappedBy = "vacancy", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private JobLocation jobLocation;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfoAboutVacancyFromEmployer> paymentAndAdditionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_requests_id", nullable = false)
    private EmployerRequests employerRequests;
}
