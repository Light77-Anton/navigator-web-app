package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "requests_for_company_setting")
public class RequestForCompanySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_info", nullable = false)
    private String requestInfo;

    @OneToOne
    @MapsId
    @JoinColumn(name = "employer_id", nullable = false)
    private EmployerRequests employerRequests;
}
