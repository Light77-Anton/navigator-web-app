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
@Table(name = "vacancy")
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profession_to_vacancy",
            joinColumns = {@JoinColumn(name = "vacancy_id")},
            inverseJoinColumns = {@JoinColumn(name = "profession_id")})
    private List<Profession> professions;

    @Column(name = "job_address", nullable = false)
    private String jobAddress;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "payment_and_additional_info", nullable = false)
    private String paymentAndAdditionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_requests_id", nullable = false)
    private EmployerRequests employerRequests;
}
