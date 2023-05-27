package com.example.navigator.model.repository;
import com.example.navigator.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    @Query(value = "SELECT l FROM com.example.navigator.model.Language AS l WHERE l.languageEndonym = :name")
    Optional<Language> findByName(String name);
}
