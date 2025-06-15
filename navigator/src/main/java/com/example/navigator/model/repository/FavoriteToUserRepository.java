package com.example.navigator.model.repository;
import com.example.navigator.model.FavoriteToUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface FavoriteToUserRepository extends JpaRepository<FavoriteToUser, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM com.example.navigator.model.FavoriteToUser AS ftu WHERE ftu.userId = :userId AND ftu.favoriteId = :favoriteId")
    void delete(long userId, long favoriteId);

    @Query(value = "SELECT ftu FROM com.example.navigator.model.FavoriteToUser AS ftu WHERE ftu.userId = :userId AND ftu.favoriteId = :favoriteId")
    Optional<FavoriteToUser> findByUserIdAndFavoriteId(long userId, long favoriteId);
}
