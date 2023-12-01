package com.example.navigator.model.repository;
import com.example.navigator.model.SavedRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface SavedRequestsRepository extends JpaRepository<SavedRequest, Long> {

    @Query(value = "SELECT sr FROM com.example.navigator.model.SavedRequest AS sr WHERE sr.user.id = :userId")
    List<SavedRequest> findAllByUserId(long userId);
}
