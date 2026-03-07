package com.sentinel.controller;

import com.sentinel.controller.base.Controller;
import com.sentinel.dto.request.MessageReq;
import com.sentinel.security.ValidateCurrentUser;
import com.sentinel.dto.response.ConvSummaryRes;
import com.sentinel.dto.response.MessageRes;
import com.sentinel.enums.FriendshipStatus;
import com.sentinel.entity.*;
import com.sentinel.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class UserMessagesController extends Controller {

    private final MessageService messageService;
    private final UserService userService;
    private final FriendshipService friendshipService;

    public UserMessagesController(MessageService messageService, ValidateCurrentUser validator,
                                  UserService userService, FriendshipService friendshipService){
        super(validator);
        this.messageService = messageService;
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @PostMapping("/send-text")
    public ResponseEntity<?> sendTextMessage(@Valid @RequestBody MessageReq request){
        UserEntity currentUserEntity = getCurrentUser();

        Optional<UserEntity> targetUser = userService.findByPublicId(request.getReceiverPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<FriendshipEntity> friendship = friendshipService.findFriendshipBetween(currentUserEntity, targetUser.get());

        if(friendship.isEmpty() || !friendship.get().getStatus().equals(FriendshipStatus.ACCEPTED)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can send messages only to friends."));
        }

        MessageEntity message = new MessageEntity();
        message.setContent(request.getContent());
        message.setSender(currentUserEntity);
        message.setReceiver(targetUser.get());

        MessageEntity saved = messageService.sendMessage(message);

        return ResponseEntity.status(HttpStatus.CREATED).body(MessageRes.fromEntity(saved));
    }

    @GetMapping("/conversation/{targetPublicId}")
    public ResponseEntity<?> getConversation(@PathVariable String targetPublicId){
        UserEntity currentUserEntity = getCurrentUser();

        Optional<UserEntity> targetUser = userService.findByPublicId(targetPublicId);

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<FriendshipEntity> friendship = friendshipService.findFriendshipBetween(currentUserEntity, targetUser.get());

        if(friendship.isEmpty() || !friendship.get().getStatus().equals(FriendshipStatus.ACCEPTED)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only view messages with friends."));
        }

        List<MessageEntity> allMessages = messageService.findConversationBetween(currentUserEntity.getId(), targetUser.get().getId());

        List<MessageRes> response = allMessages.stream().map(MessageRes::fromEntity).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all-conversations")
    public ResponseEntity<?> getAllConversations(){
        UserEntity currentUserEntity = getCurrentUser();
        List<ConvSummaryRes> allConversations = messageService.findAllConversations(currentUserEntity.getId());
        return ResponseEntity.ok(allConversations);
    }

    @DeleteMapping("/remove-conversation/{targetPublicId}")
    public ResponseEntity<?> removeConversation(@PathVariable String targetPublicId){
        UserEntity currentUserEntity = getCurrentUser();

        Optional<UserEntity> targetUser = userService.findByPublicId(targetPublicId);

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<FriendshipEntity> friendship = friendshipService.findFriendshipBetween(currentUserEntity, targetUser.get());

        if(friendship.isEmpty() || !friendship.get().getStatus().equals(FriendshipStatus.ACCEPTED)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("Error", "You can delete the conversations only with friends."));
        }

        messageService.removeBySenderAndReceiver(currentUserEntity.getId(), targetUser.get().getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
