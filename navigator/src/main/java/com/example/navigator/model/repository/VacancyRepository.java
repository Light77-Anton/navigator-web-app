package com.example.navigator.model.repository;
import com.example.navigator.model.Vacancy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ltue.user_id = u.id AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius)"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacanciesWithLanguagesMatching(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ltue.user_id = u.id AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacanciesWithLanguagesMatchingSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ltue.user_id = u.id AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacanciesWithLanguagesMatchingSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "INNER JOIN languages_to_users ltumy ON ltumy.user_id = :myId " +
            "INNER JOIN languages_to_users ltue ON ltue.user_id = u.id AND ltumy.language_id = ltue.language_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ltue.user_id = u.id AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography ASC"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacanciesWithLanguagesMatchingSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius)"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacancies(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius) " +
            "ORDER BY u.name DESC"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacanciesSortedByName(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius) " +
            "ORDER BY u.ranking DESC"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacanciesSortedByRating(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);

    @Query(value = "SELECT v FROM vacancies v " +
            "INNER JOIN job_locations jl ON jl.vacancy_id = v.id " +
            "INNER JOIN profession_to_vacancy ptv ON ptv.profession_id = :professionId " +
            "INNER JOIN users u ON u.id = v.employer_requests_id " +
            "WHERE u.is_blocked = false AND u.is_activated = true AND ptv.vacancy_id = v.id AND " +
            "ST_DWithin(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography, :radius) " +
            "ORDER BY ST_Distance(ST_MakePoint(:myLongitude, :myLatitude)::geography, ST_MakePoint(jl.longitude, jl.latitude)::geography ASC"
            ,
            nativeQuery = true)
    List<Vacancy> findAllVacanciesSortedByLocation(
            long professionId,
            long myId,
            double myLatitude,
            double myLongitude,
            int radius,
            Pageable pageable);
}
