package com.example.navigator.model.repository;
import com.example.navigator.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query(value = "SELECT v FROM com.example.navigator.model.Vote AS v WHERE v.user.id = :userId")
    List<Vote> findAllByUserId(long userId);
}
