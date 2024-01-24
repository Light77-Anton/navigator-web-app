package com.example.navigator.model.repository;
import com.example.navigator.model.InfoAboutVacancyFromEmployer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InfoAboutVacancyFromEmployerRepository extends JpaRepository<InfoAboutVacancyFromEmployer, Long> {

    @Query(value = "SELECT iavfe FROM com.example.navigator.model.InfoAboutVacancyFromEmployer AS iavfe " +
            "WHERE iavfe.language.languageEndonym = :language AND iavfe.vacancy.id = :vacancyId")
    Optional<InfoAboutVacancyFromEmployer> findByVacancyIdAndLanguage(long vacancyId, String language);
}
