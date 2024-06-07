package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "last_requests")
public class LastRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_request_id", nullable = false)
    private Profession profession;

    @Column(name = "limit", nullable = false)
    private int limit;

    @Column(name = "is_auto", nullable = false)
    private boolean isAuto;

    @Column(name = "are_languages_matched", nullable = false)
    private boolean areLanguagesMatched;

    @Column(name = "in_radius_of", nullable = false)
    private int inRadiusOf;

    @Column(name = "sort_type")
    private String SortType;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
