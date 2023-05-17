package com.example.navigator.model.repository;

import com.example.navigator.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c FROM com.example.navigator.model.Comment AS c WHERE c.toId = :toId AND c.fromId = :fromId")
    Optional<Comment> findByToIdAndFromId(long toId, long fromId);
}
