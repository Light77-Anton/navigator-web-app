package com.example.navigator.model.repository;
import com.example.navigator.model.BannedToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface BannedToUserRepository extends JpaRepository<BannedToUser, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM com.example.navigator.model.BannedToUser AS btu WHERE btu.userId = :userId AND btu.bannedId = :bannedId")
    void delete(long userId, long bannedId);

    @Query(value = "SELECT btu FROM com.example.navigator.model.BannedToUser AS btu WHERE btu.userId = :userId AND btu.bannedId = :bannedId")
    Optional<BannedToUser> findByUserIdAndBannedId(long userId, long bannedId);
}
