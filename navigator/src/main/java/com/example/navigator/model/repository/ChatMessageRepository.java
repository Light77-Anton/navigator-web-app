package com.example.navigator.model.repository;

import com.example.navigator.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "SELECT COUNT(m) FROM com.example.navigator.model.ChatMessage AS m " +
            "WHERE m.sender.id = :senderId AND m.recipient.id = :recipientId AND m.status = 'RECEIVED'")
    long countNewMessages(long senderId, long recipientId);

    @Query(value = "SELECT m FROM com.example.navigator.model.ChatMessage AS m " +
            "WHERE m.sender.id = :senderId AND m.recipient.id = :recipientId")
    List<ChatMessage> findAllBySenderIdAndRecipientId(long senderId, long recipientId);
}
