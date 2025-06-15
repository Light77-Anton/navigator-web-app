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

    //____________________________ получение рабочих

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguageAndIncludingTemporarilyInactive(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguageAndIncludingTemporarilyInactiveSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguageAndIncludingTemporarilyInactiveSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguageAndIncludingTemporarilyInactiveSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguage(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguageSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguageSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndAdditionalLanguageSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatching(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployees(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND ed.status = 1 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployeesIncludingTemporarilyInactive(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesIncludingTemporarilyInactiveSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesIncludingTemporarilyInactiveSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesIncludingTemporarilyInactiveSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);


    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguage(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguageSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguageSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguageSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltue.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndIncludingTemporarilyInactive(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltue.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndIncludingTemporarilyInactiveSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltue.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndIncludingTemporarilyInactiveSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed " +
            "AND ltue.user_id = u.id AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithLanguagesMatchingAndIncludingTemporarilyInactiveSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius)"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguageAndIncludingTemporarilyInactive(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguageAndIncludingTemporarilyInactiveSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguageAndIncludingTemporarilyInactiveSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT u FROM users u " +
            "INNER JOIN employees_data ed ON ed.id = u.id " +
            "INNER JOIN users_locations ul ON ul.user_id = u.id " +
            "INNER JOIN professions_to_users ptu ON ptu.profession_id = :professionId " +
            "INNER JOIN languages_to_users ltual ON ltual.language_id = :additionalLanguageId " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ed.is_auto = :isAuto AND " +
            "ed.is_multivacancy_allowed = :isMultivacancyAllowed AND ltual.user_id = u.id " +
            "AND ptu.employee_id = u.id AND NOT ed.status = 0 AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(ul.longitude, ul.latitude)::geography ASC"
            , nativeQuery = true)
    List<User> findAllEmployeesWithAdditionalLanguageAndIncludingTemporarilyInactiveSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            long additionalLanguageId,
            boolean isAuto,
            boolean isMultivacancyAllowed,
            int radius,
            Pageable pageable);
    //____________________________

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
