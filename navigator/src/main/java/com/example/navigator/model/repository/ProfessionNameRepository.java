package com.example.navigator.model.repository;
import com.example.navigator.model.ProfessionName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionNameRepository extends JpaRepository<ProfessionName, Long> {

    @Query(value = "SELECT pn FROM com.example.navigator.model.ProfessionName AS pn WHERE pn.professionName = :name")
    Optional<ProfessionName> findByName(String name);

    @Query(value = "SELECT pn FROM com.example.navigator.model.ProfessionName AS pn WHERE pn.professionName = :name " +
            "AND pn.language.languageEndonym = :language")
    Optional<ProfessionName> findByNameAndLanguage(String name, String language);

    @Query(value = "SELECT pn FROM com.example.navigator.model.ProfessionName AS pn " +
    "INNER JOIN pn.language AS l WITH l.languageEndonym = :language " +
    "INNER JOIN pn.profession AS p WITH p.id = :professionId")
    Optional<ProfessionName> findByProfessionIdAndLanguage(long professionId, String language);

    @Query(value = "SELECT pn FROM com.example.navigator.model.ProfessionName AS pn " +
    "INNER JOIN pn.language AS l WITH l.languageEndonym = :language")
    List<ProfessionName> findAllBySpecifiedLanguage(String language);

    @Query(value = "SELECT pn FROM com.example.navigator.model.ProfessionName AS pn WHERE pn.professionName = :name")
    Optional<ProfessionName> findByName(long userId, long professionId);
}
