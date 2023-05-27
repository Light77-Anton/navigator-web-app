package com.example.navigator.model.repository;
import com.example.navigator.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query(value = "SELECT j FROM com.example.navigator.model.Job AS j " +
            "INNER JOIN j.employerRequests AS er " +
            "WHERE er.id = :id AND j.status = 'NOT CONFIRMED'")
    Optional<Job> findByEmployerRequestsId(long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM com.example.navigator.model.Job AS j " +
            "WHERE j.status = 'NOT CONFIRMED' AND j.expirationTime >= :currentTimeMillis")
    void deleteAllNotConfirmedJobsByExpirationTime(long currentTimeMillis);
}
