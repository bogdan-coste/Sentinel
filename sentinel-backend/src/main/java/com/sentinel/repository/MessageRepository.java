package com.sentinel.repository;

import com.sentinel.entity.MessageEntity;
import com.sentinel.entity.UserEntity;
import com.sentinel.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("SELECT m FROM MessageEntity m WHERE " +
            "((m.sender.id = :user1 AND m.receiver.id = :user2)" + " OR " +
            "(m.sender.id = :user2 AND m.receiver.id = :user1))" + " AND " +
            "m.deletedBySender = false AND m.deletedByReceiver = false " +
            "ORDER BY m.createdAt DESC")
    List<MessageEntity> findConversationBetween(Long user1, Long user2);

    @Query("SELECT EXISTS(SELECT 1 FROM MessageEntity m WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id))")
    boolean existsBySenderAndReceiver(Long user1Id, Long user2Id);

    @Query("SELECT count(m) FROM MessageEntity m WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id) AND (m.status <> 'READ')")
    long findAllUnreadMessages(Long user1Id, Long user2Id);

    @Query("SELECT m FROM MessageEntity m WHERE " +
            "((m.sender.id = :user1 AND m.receiver.id = :user2) OR " +
            "(m.sender.id = :user2 AND m.receiver.id = :user1)) " +
            "ORDER BY m.createdAt DESC")
    Page<MessageEntity> findLastMessageBetween(@Param("user1") Long user1,
                                               @Param("user2") Long user2,
                                               Pageable pageable);

    @Modifying
    @Query("UPDATE MessageEntity m SET m.status = :status WHERE m.id = :id AND m.receiver.id = :receiverId")
    int updateMessageStatus(Long id, Long receiverId, MessageStatus status);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId then m.receiver ELSE m.sender END FROM MessageEntity m WHERE (m.sender.id = :userId OR m.receiver.id = :userId)")
    List<UserEntity> findALlUserConversations(@Param("userId") Long userId);

    @Query(value = "SELECT DISTINCT u.* FROM users u " +
            "JOIN messages m ON (m.sender_id = u.id OR m.receiver_id = u.id) " +
            "WHERE (m.sender_id = :userId OR m.receiver_id = :userId) AND u.id != :userId",
            nativeQuery = true)
    List<UserEntity> findConversationPartners(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM MessageEntity m WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id)")
    void removeBySenderAndReceiver(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("""
        SELECT m FROM MessageEntity m\s
        WHERE m.id IN (
            SELECT MAX(m2.id)\s
            FROM MessageEntity m2\s
            WHERE (m2.sender.id = :userId OR m2.receiver.id = :userId)
            AND (
                (m2.sender.id = :userId AND m2.deletedBySender = false) OR\s
                (m2.receiver.id = :userId AND m2.deletedByReceiver = false)
            )
            GROUP BY CASE\s
                WHEN m2.sender.id = :userId THEN m2.receiver.id\s
                ELSE m2.sender.id\s
            END
        )
        ORDER BY m.createdAt DESC
    """)
    List<MessageEntity> findLastMessagesForAllThreads(@Param("userId") Long userId);
}
