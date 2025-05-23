package com.example.navigator.model.repository;
import com.example.navigator.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.TreeSet;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "SELECT COUNT(m) FROM com.example.navigator.model.ChatMessage AS m " +
            "WHERE m.sender.id = :senderId AND m.recipient.id = :recipientId AND m.status = 'RECEIVED'")
    long countNewMessages(long senderId, long recipientId);

    @Transactional
    @Modifying
    @Query(value = "DELETE m FROM com.example.navigator.model.ChatMessage AS m " +
            "WHERE m.sender.id = :userId OR m.recipient.id = :userId")
    void deleteAllByUserId(long userId);

    @Query(value = "SELECT m FROM com.example.navigator.model.ChatMessage AS m " +
            "WHERE m.sender.id = :senderId AND m.recipient.id = :recipientId")
    TreeSet<ChatMessage> findAllBySenderIdAndRecipientId(long senderId, long recipientId);

    @Query(value = "SELECT m FROM com.example.navigator.model.ChatMessage AS m " +
            "WHERE m.isImage = true AND m.content = null")
    Optional<ChatMessage> getLastImageForPathSetting();
}
