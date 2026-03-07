package com.sentinel.controller;

import com.sentinel.controller.base.Controller;
import com.sentinel.security.ValidateCurrentUser;
import com.sentinel.dto.request.FriendshipReq;
import com.sentinel.enums.FriendshipStatus;
import com.sentinel.entity.*;
import com.sentinel.service.FriendshipService;
import com.sentinel.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/friends")
public class UserFriendshipController extends Controller {

    private final FriendshipService friendshipService;
    private final UserService userService;

    public UserFriendshipController(FriendshipService friendshipService, UserService userService,
                                    ValidateCurrentUser validator){
        super(validator);
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/add-friend")
    public ResponseEntity<?> addFriend(@RequestBody FriendshipReq request){
        UserEntity currentUserEntity = getCurrentUser();

        Optional<UserEntity> targetUser = userService.findByPublicId(request.getPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(currentUserEntity.getId().equals(targetUser.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "You can't add youself as a friend."));
        }

        Optional<FriendshipEntity> existingFriendship = friendshipService.findFriendshipBetween(currentUserEntity, targetUser.get());
        if(existingFriendship.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("Error", "A friendship or pending request already exists between these users."));
        }

        Optional<FriendshipEntity> friendship = friendshipService.createFriendship(currentUserEntity, targetUser.get());

        if(friendship.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("Error", "Failed to create friend request."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("friendshipId", friendship.get().getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("/remove-friend")
    public ResponseEntity<?> removeFriend(@RequestBody FriendshipReq request){
        UserEntity currentUserEntity = getCurrentUser();

        Optional<UserEntity> targetUser = userService.findByPublicId(request.getPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(currentUserEntity.getId().equals(targetUser.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "You can't remove yourself as a friend."));
        }

        Optional<FriendshipEntity> friendship = friendshipService.findFriendshipBetween(currentUserEntity, targetUser.get());

        if(friendship.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        boolean wasAccepted = friendship.get().getStatus().equals(FriendshipStatus.ACCEPTED);

        friendshipService.deleteFriendship(friendship.get());

        if(wasAccepted){

            int targetUserFriendCount = targetUser.get().getFriendCount();
            targetUserFriendCount--;
            targetUser.get().setFriendCount(targetUserFriendCount);
            userService.save(targetUser.get());

            int currentUserFriendCount = currentUserEntity.getFriendCount();
            currentUserFriendCount--;
            currentUserEntity.setFriendCount(currentUserFriendCount);
            userService.save(currentUserEntity);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/accept-request")
    public ResponseEntity<?> acceptRequest(@RequestBody FriendshipReq request){
        UserEntity currentUserEntity = getCurrentUser();

        Optional<UserEntity> targetUser = userService.findByPublicId(request.getPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(currentUserEntity.getId().equals(targetUser.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "You can't accept your own friend request."));
        }

        Optional<FriendshipEntity> friendship = friendshipService.findFriendshipBetween(currentUserEntity, targetUser.get());

        if(friendship.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (friendship.get().getStatus() != FriendshipStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Friend request is not pending or already accepted"));
        }

        int currentUserFriendCount = currentUserEntity.getFriendCount();
        currentUserFriendCount++;
        currentUserEntity.setFriendCount(currentUserFriendCount);
        userService.save(currentUserEntity);

        int targetUserFriendCount = targetUser.get().getFriendCount();
        targetUserFriendCount++;
        targetUser.get().setFriendCount(targetUserFriendCount);
        userService.save(targetUser.get());

        friendship.get().setStatus(FriendshipStatus.ACCEPTED);
        friendshipService.save(friendship.get());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("Success", "Friend request accepted."));
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<?> getPendingRequests() {
        UserEntity currentUserEntity = getCurrentUser();

        List<FriendshipEntity> pendingIncomingRequests = friendshipService.findReceivedPendingRequests(currentUserEntity);

        List<Map<String, Object>> incomingRequestMapList = pendingIncomingRequests.stream()
                .map(friendship -> {
                    Map<String, Object> requestMap = new HashMap<>();
                    UserEntity sender = friendship.getUserEntity1().getId().equals(currentUserEntity.getId())
                            ? friendship.getUserEntity2()
                            : friendship.getUserEntity1();
                    requestMap.put("publicId", sender.getPublicId());
                    requestMap.put("name", sender.getName());
                    requestMap.put("username", sender.getUsername());
                    if (sender.getCurrentProfilePicURL() != null) {
                        requestMap.put("profilePicUrl", "/uploads/public/" + sender.getPublicId() + "/profile/" + sender.getCurrentProfilePicURL());
                    } else {
                        requestMap.put("profilePicUrl", null);
                    }
                    requestMap.put("friendshipId", friendship.getId());
                    return requestMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(incomingRequestMapList);
    }

    @GetMapping("/my-friends")
    public ResponseEntity<?> getMyFriends() {
        UserEntity currentUserEntity = getCurrentUser();

        List<UserEntity> myFriends = friendshipService.findFriends(currentUserEntity);

        List<Map<String, Object>> response = myFriends.stream().map(friend -> {
            Map<String, Object> map = new HashMap<>();
            map.put("publicId", friend.getPublicId());
            map.put("name", friend.getName());
            map.put("username", friend.getUsername());
            if (friend.getCurrentProfilePicURL() != null) {
                map.put("image", "/uploads/public/" + friend.getPublicId() + "/profile/" + friend.getCurrentProfilePicURL());
            } else {
                map.put("image", null);
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
