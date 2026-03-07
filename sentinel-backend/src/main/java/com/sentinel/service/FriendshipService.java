package com.sentinel.service;

import com.sentinel.entity.FriendshipEntity;
import com.sentinel.entity.UserEntity;
import com.sentinel.enums.FriendshipStatus;
import com.sentinel.repository.FriendshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {

    private final FriendshipRepository repo;

    public FriendshipService(FriendshipRepository repo){
        this.repo = repo;
    }

    public Optional<FriendshipEntity> createFriendship(UserEntity userEntityA, UserEntity userEntityB) {
        UserEntity userEntity1 = userEntityA.getId() < userEntityB.getId() ? userEntityA : userEntityB;
        UserEntity userEntity2 = userEntity1 == userEntityA ? userEntityB : userEntityA;

        Optional<FriendshipEntity> existing = repo.findByUser1AndUser2(userEntity1, userEntity2);
        if (existing.isPresent()) {
            return Optional.empty();
        }

        FriendshipEntity newFriendshipEntity = new FriendshipEntity();
        newFriendshipEntity.setUserEntity1(userEntity1);
        newFriendshipEntity.setUserEntity2(userEntity2);
        newFriendshipEntity.setStatus(FriendshipStatus.PENDING);
        repo.save(newFriendshipEntity);
        return Optional.of(newFriendshipEntity);
    }

    public Optional<FriendshipEntity> areFriends(UserEntity userEntity1, UserEntity userEntity2){
        return repo.findByUser1AndUser2(userEntity1, userEntity2);
    }

    public List<UserEntity> findFriends(UserEntity userEntity){
        return repo.findFriends(userEntity);
    }

    public boolean existsByUser1AndUser2(UserEntity userEntity1, UserEntity userEntity2){
        return repo.existsByUser1AndUser2(userEntity1, userEntity2);
    }

    public Optional<FriendshipEntity> findFriendshipBetween(UserEntity userEntity1, UserEntity userEntity2) {
        return repo.findFriendshipBetween(userEntity1, userEntity2);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFriendship(FriendshipEntity friendshipEntity) {
        repo.delete(friendshipEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(FriendshipEntity friendshipEntity){
        repo.save(friendshipEntity);
    }

    public List<FriendshipEntity> findReceivedPendingRequests(UserEntity userEntity){
        return repo.findPendingRequestsForUser(userEntity);

    }
}
