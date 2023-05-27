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

    @Query(value = "SELECT pn FROM com.example.navigator.model.ProfessionName AS pn WHERE pn.language.languageEndonym = :language")
    List<ProfessionName> findAllBySpecifiedLanguage(String language);
}
