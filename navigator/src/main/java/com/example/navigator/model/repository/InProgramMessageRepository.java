package com.example.navigator.model.repository;
import com.example.navigator.model.InProgramMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InProgramMessageRepository extends JpaRepository<InProgramMessage, Long> {

    @Query(value = "SELECT ipm FROM com.example.navigator.model.InProgramMessage AS ipm " +
            "WHERE ipm.language.languageEndonym  = :language AND ipm.messagesCodeName.codeName = :codeName")
    Optional<InProgramMessage> findByCodeNameAndLanguage(String codeName, String language);
}
