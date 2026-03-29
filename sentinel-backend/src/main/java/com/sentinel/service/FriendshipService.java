package com.sentinel.service;

import com.sentinel.model.Friendship;
import com.sentinel.model.FriendshipStatus;
import com.sentinel.model.User;
import com.sentinel.repository.FriendshipRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {

    private final FriendshipRepository repo;

    public FriendshipService(FriendshipRepository repo){
        this.repo = repo;
    }

    public Optional<Friendship> createFriendship(User userA, User userB) {
        User user1 = userA.getId() < userB.getId() ? userA : userB;
        User user2 = user1 == userA ? userB : userA;

        Optional<Friendship> existing = repo.findByUser1AndUser2(user1, user2);
        if (existing.isPresent()) {
            return Optional.empty();
        }

        Friendship newFriendship = new Friendship();
        newFriendship.setUser1(user1);
        newFriendship.setUser2(user2);
        newFriendship.setStatus(FriendshipStatus.PENDING);
        repo.save(newFriendship);
        return Optional.of(newFriendship);
    }

    public Optional<Friendship> areFriends(User user1, User user2){
        return repo.findByUser1AndUser2(user1, user2);
    }

    public List<User> findFriends(User user){
        return repo.findFriends(user);
    }

    public boolean existsByUser1AndUser2(User user1, User user2){
        return repo.existsByUser1AndUser2(user1, user2);
    }

    public Optional<Friendship> findFriendshipBetween(User user1, User user2) {
        return repo.findFriendshipBetween(user1, user2);
    }

    public void deleteFriendship(Friendship friendship) {
        repo.delete(friendship);
    }

    public void save(Friendship friendship){
        repo.save(friendship);
    }

    public List<Friendship> findReceivedPendingRequests(User user){
        return repo.findPendingRequestsForUser(user);

    }
}
