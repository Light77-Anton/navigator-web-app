package com.example.navigator.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "saved_requests")
public class SavedRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "profession_name", nullable = false)
    private String professionName;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
