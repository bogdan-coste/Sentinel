package com.sentinel.repository;

import com.sentinel.model.Friendship;
import com.sentinel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2)")
    int deleteFriendshipBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) AND f.status = 'ACCEPTED'")
    Optional<Friendship> findByUser1AndUser2(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT u FROM User u WHERE " +
            "u IN (SELECT f.user2 FROM Friendship f WHERE f.user1 = :user AND f.status = 'ACCEPTED') " +
            "OR " +
            "u IN (SELECT f.user1 FROM Friendship f WHERE f.user2 = :user AND f.status = 'ACCEPTED')")
    List<User> findFriends(@Param("user") User user);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    boolean existsByUser1AndUser2(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT f FROM Friendship f WHERE ((f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1))")
    Optional<Friendship> findFriendshipBetween(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT f FROM Friendship f WHERE f.user1 = :user AND f.status = 'PENDING'")
    List<Friendship> findSentPendingRequests(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = 'PENDING'")
    List<Friendship> findPendingRequestsForUser(@Param("user") User user);

}
