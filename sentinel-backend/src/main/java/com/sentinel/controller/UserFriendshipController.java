package com.sentinel.controller;

import com.sentinel.Validators.ValidateCurrentUser;
import com.sentinel.model.*;
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
public class UserFriendshipController extends Controller{

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
    public ResponseEntity<?> addFriend(@RequestBody FriendshipRequestDto request){
        User currentUser = getCurrentUser();

        Optional<User> targetUser = userService.findByPublicId(request.getPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(currentUser.getId().equals(targetUser.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "You can't add youself as a friend."));
        }

        Optional<Friendship> existingFriendship = friendshipService.findFriendshipBetween(currentUser, targetUser.get());
        if(existingFriendship.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("Error", "A friendship or pending request already exists between these users."));
        }

        Optional<Friendship> friendship = friendshipService.createFriendship(currentUser, targetUser.get());

        if(friendship.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("Error", "Failed to create friend request."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("friendshipId", friendship.get().getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("/remove-friend")
    public ResponseEntity<?> removeFriend(@RequestBody FriendshipRequestDto request){
        User currentUser = getCurrentUser();

        Optional<User> targetUser = userService.findByPublicId(request.getPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(currentUser.getId().equals(targetUser.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "You can't remove yourself as a friend."));
        }

        Optional<Friendship> friendship = friendshipService.findFriendshipBetween(currentUser, targetUser.get());

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

            int currentUserFriendCount = currentUser.getFriendCount();
            currentUserFriendCount--;
            currentUser.setFriendCount(currentUserFriendCount);
            userService.save(currentUser);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/accept-request")
    public ResponseEntity<?> acceptRequest(@RequestBody FriendshipRequestDto request){
        User currentUser = getCurrentUser();

        Optional<User> targetUser = userService.findByPublicId(request.getPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if(currentUser.getId().equals(targetUser.get().getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", "You can't accept your own friend request."));
        }

        Optional<Friendship> friendship = friendshipService.findFriendshipBetween(currentUser, targetUser.get());

        if(friendship.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (friendship.get().getStatus() != FriendshipStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Friend request is not pending or already accepted"));
        }

        int currentUserFriendCount = currentUser.getFriendCount();
        currentUserFriendCount++;
        currentUser.setFriendCount(currentUserFriendCount);
        userService.save(currentUser);

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
        User currentUser = getCurrentUser();

        List<Friendship> pendingIncomingRequests = friendshipService.findReceivedPendingRequests(currentUser);

        List<Map<String, Object>> incomingRequestMapList = pendingIncomingRequests.stream()
                .map(friendship -> {
                    Map<String, Object> requestMap = new HashMap<>();
                    User sender = friendship.getUser1().getId().equals(currentUser.getId())
                            ? friendship.getUser2()
                            : friendship.getUser1();
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
        User currentUser = getCurrentUser();

        List<User> myFriends = friendshipService.findFriends(currentUser);

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
