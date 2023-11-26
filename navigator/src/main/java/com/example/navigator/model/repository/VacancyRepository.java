package com.example.navigator.model.repository;
import com.example.navigator.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    @Query(value = "SELECT v FROM com.example.navigator.model.Vacancy AS v " +
            "INNER JOIN v.professions.professionNames AS pn ON pn.professionName = :profession ")
    List<Vacancy> findByProfession(String profession);

    @Query(value = "SELECT v FROM com.example.navigator.model.Vacancy AS v " +
            "INNER JOIN v.professions.professionNames AS pn ON pn.professionName = :profession " +
            "INNER JOIN v.employerRequests.employer AS e ORDER BY e.name DESC")
    List<Vacancy> findAllByProfessionSortedByName(String profession);

    @Query(value = "SELECT v FROM com.example.navigator.model.Vacancy AS v " +
            "INNER JOIN v.professions.professionNames AS pn ON pn.professionName = :profession " +
            "INNER JOIN v.employerRequests.employer AS e ORDER BY e.ranking DESC")
    List<Vacancy> findAllByProfessionSortedByRating(String profession);
}
