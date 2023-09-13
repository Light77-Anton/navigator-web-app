package com.example.navigator.model.repository;
import com.example.navigator.model.LanguageToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LanguageToUserRepository extends JpaRepository<LanguageToUser, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM com.example.navigator.model.LanguageToUser WHERE user_id = :userId")
    void deleteByUserId(long userId);
}
