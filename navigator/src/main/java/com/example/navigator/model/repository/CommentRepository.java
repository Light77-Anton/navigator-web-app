package com.example.navigator.model.repository;
import com.example.navigator.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c FROM com.example.navigator.model.Comment AS c WHERE c.toId = :toId AND c.fromId = :fromId")
    Optional<Comment> findByToIdAndFromId(long toId, long fromId);

    @Query(value = "SELECT c FROM com.example.navigator.model.Comment AS c WHERE c.recipient.id = :recipientId")
    List<Comment> findByRecipientId(long recipientId);

    @Query(value = "SELECT c FROM com.example.navigator.model.Comment AS c WHERE c.recipient.id = :recipientId ORDER BY c.averageVote ASC")
    List<Comment> findByRecipientIdNegativeFirst(long recipientId);

    @Query(value = "SELECT c FROM com.example.navigator.model.Comment AS c WHERE c.recipient.id = :recipientId ORDER BY c.averageVote DESC")
    List<Comment> findByRecipientIdPositiveFirst(long recipientId);
}
