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
@Table(name = "employers_passive_search_data")
public class EmployerPassiveSearchData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "professions_to_passive_search",
            joinColumns = {@JoinColumn(name = "passive_search_id")},
            inverseJoinColumns = {@JoinColumn(name = "profession_id")})
    private List<Profession> professions;

    @Column(name = "job_address", nullable = false)
    private String jobAddress;

    @Column(name = "designated_date_time")
    private LocalDateTime designatedDateTime;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(name = "payment_and_additional_info", nullable = false)
    private String paymentAndAdditionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_requests_id", nullable = false)
    private EmployerRequests employerRequests;
}
