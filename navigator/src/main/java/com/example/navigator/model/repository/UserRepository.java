package com.example.navigator.model.repository;
import com.example.navigator.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE com.example.navigator.model.User AS u SET u.ranking = :newRanking WHERE id = :id")
    void vote(long id, byte newRanking);

    @Query(value = "SELECT u.avatar FROM com.example.navigator.model.User AS u WHERE u.id = :id")
    Optional<String> findAvatarPath(long id);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u.isBlocked FROM com.example.navigator.model.User AS u WHERE u.id = :id")
    byte userIsBlocked(long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE com.example.navigator.model.User AS u SET u.isBlocked = :isBlocked WHERE u.id = :id")
    void changeUserCondition(long id, byte isBlocked);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false")
    List<User> findAllByProfession(long professionId, Pageable pageable);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false ORDER BY u.name DESC")
    List<User> findAllByProfessionSortedByName(long professionId, Pageable pageable);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false AND u.employeeData.isAuto = true")
    List<User> findAllByProfessionAndAuto(long professionId, Pageable pageable);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false AND u.employeeData.isAuto = true ORDER BY u.name DESC")
    List<User> findAllByProfessionAndAutoSortedByName(long professionId, Pageable pageable);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false")
    List<User> findAllByProfession(long professionId);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false AND u.employeeData.isAuto = true")
    List<User> findAllByProfessionAndAuto(long professionId);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false ORDER BY u.ranking DESC")
    List<User> findTheBestByProfession(long professionId, Pageable pageable);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u " +
            "INNER JOIN u.employeeData.professions AS p WITH p.id = :professionId " +
            "WHERE u.isBlocked = false AND u.employeeData.isAuto = true ORDER BY u.ranking DESC")
    List<User> findTheBestByProfessionAndAuto(long professionId, Pageable pageable);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u WHERE u.id = :id")
    Optional<User> findById(long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE com.example.navigator.model.User AS u SET u.restoreCode = :code WHERE u.id = :id")
    void addRestoreCode(long id, String code);

    @Query(value = "SELECT u FROM com.example.navigator.model.User AS u WHERE u.restoreCode = :restoreCode")
    Optional<User> findByCode(String restoreCode);

    @Transactional
    @Modifying
    @Query(value = "UPDATE com.example.navigator.model.User AS u SET u.password = :newPassword WHERE u.restoreCode = :code")
    void findByCodeAndUpdatePassword(String code, String newPassword);
}
