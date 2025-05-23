package com.example.navigator.model.repository;
import com.example.navigator.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query(value = "SELECT cr FROM com.example.navigator.model.ChatRoom AS cr " +
            "WHERE cr.senderId = :senderId AND cr.recipientId = :recipientId")
    Optional<ChatRoom> findBySenderIdAndRecipientId(long senderId, long recipientId);

    @Transactional
    @Modifying
    @Query(value = "DELETE cr FROM com.example.navigator.model.ChatRoom AS cr " +
            "WHERE cr.senderId = :userId OR cr.recipientId = :userId")
    void deleteAllByUserId(long userId);
}
