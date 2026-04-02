package com.sentinel.repository;

import com.sentinel.entity.FriendshipEntity;
import com.sentinel.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM FriendshipEntity f WHERE (f.userEntity1 = :user1 AND f.userEntity2 = :user2)")
    int deleteFriendshipBetweenUsers(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);

    @Query("SELECT f FROM FriendshipEntity f WHERE (f.userEntity1 = :user OR f.userEntity2 = :user) AND f.status = 'ACCEPTED'")
    List<FriendshipEntity> findAcceptedFriendships(@Param("user") UserEntity user);

    @Query("SELECT f FROM FriendshipEntity f WHERE (f.userEntity1 = :user1 AND f.userEntity2 = :user2) AND f.status = 'ACCEPTED'")
    Optional<FriendshipEntity> findByUser1AndUser2(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "u IN (SELECT f.userEntity2 FROM FriendshipEntity f WHERE f.userEntity1 = :user AND f.status = 'ACCEPTED') " +
            "OR " +
            "u IN (SELECT f.userEntity1 FROM FriendshipEntity f WHERE f.userEntity2 = :user AND f.status = 'ACCEPTED')")
    List<UserEntity> findFriends(@Param("user") UserEntity user);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM FriendshipEntity f " +
            "WHERE (f.userEntity1 = :user1 AND f.userEntity2 = :user2) OR (f.userEntity1 = :user2 AND f.userEntity2 = :user1)")
    boolean existsByUser1AndUser2(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);

    @Query("SELECT f FROM FriendshipEntity f WHERE " +
            "(f.userEntity1 = :user1 AND f.userEntity2 = :user2) OR " +
            "(f.userEntity1 = :user2 AND f.userEntity2 = :user1)")
    Optional<FriendshipEntity> findFriendshipBetween(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);

    @Query("SELECT f FROM FriendshipEntity f WHERE f.userEntity1 = :user AND f.status = 'PENDING'")
    List<FriendshipEntity> findSentPendingRequests(@Param("user") UserEntity user);

    @Query("SELECT f FROM FriendshipEntity f WHERE (f.userEntity1 = :user OR f.userEntity2 = :user) AND f.status = 'PENDING'")
    List<FriendshipEntity> findPendingRequestsForUser(@Param("user") UserEntity user);
}