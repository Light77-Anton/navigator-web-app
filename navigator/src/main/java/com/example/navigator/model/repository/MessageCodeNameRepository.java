package com.example.navigator.model.repository;

import com.example.navigator.model.MessagesCodeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageCodeNameRepository extends JpaRepository<MessagesCodeName, Long> {

    @Query(value = "SELECT mcn FROM com.example.navigator.model.MessagesCodeName AS mcn WHERE mcn.codeName = :codeName")
    Optional<MessagesCodeName> findByName(String codeName);
}
