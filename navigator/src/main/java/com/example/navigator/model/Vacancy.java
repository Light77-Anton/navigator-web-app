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

    @Column(name = "quotas_number", nullable = false)
    private int quotasNumber;

    @Column(name = "type", nullable = false) // public, private, template
    private String type;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "is_necessary_to_close_all_quotas")
    private boolean isNecessaryToCloseAllQuotas;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chat_message_id", referencedColumnName = "id")
    private ChatMessage referencedChatMessage;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "employee_to_vacancy",
            joinColumns = {@JoinColumn(name = "vacancy_id")},
            inverseJoinColumns = {@JoinColumn(name = "employee_id")})
    private List<EmployeeData> hiredEmployees;

    @OneToOne(mappedBy = "vacancy", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private JobLocation jobLocation;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "waiting_date_time")
    private LocalDateTime waitingDateTime;

    @Column(name = "payment_and_additional_info")
    private String paymentAndAdditionalInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_requests_id", nullable = false)
    private EmployerRequests employerRequests;
}
