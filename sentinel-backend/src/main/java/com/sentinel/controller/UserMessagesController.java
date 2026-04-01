package com.sentinel.controller;

import com.sentinel.Validators.ValidateCurrentUser;
import com.sentinel.model.*;
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
public class UserMessagesController extends Controller{

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
    public ResponseEntity<?> sendTextMessage(@Valid @RequestBody MessageRequestDto request){
        User currentUser = getCurrentUser();

        Optional<User> targetUser = userService.findByPublicId(request.getReceiverPublicId());

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Friendship> friendship = friendshipService.findFriendshipBetween(currentUser, targetUser.get());

        if(friendship.isEmpty() || !friendship.get().getStatus().equals(FriendshipStatus.ACCEPTED)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can send messages only to friends."));
        }

        MessageEntity message = new MessageEntity();
        message.setContent(request.getContent());
        message.setSender(currentUser);
        message.setReceiver(targetUser.get());

        MessageEntity saved = messageService.sendMessage(message);

        return ResponseEntity.status(HttpStatus.CREATED).body(MessageDto.fromEntity(saved));
    }

    @GetMapping("/conversation/{targetPublicId}")
    public ResponseEntity<?> getConversation(@PathVariable String targetPublicId){
        User currentUser = getCurrentUser();

        Optional<User> targetUser = userService.findByPublicId(targetPublicId);

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Friendship> friendship = friendshipService.findFriendshipBetween(currentUser, targetUser.get());

        if(friendship.isEmpty() || !friendship.get().getStatus().equals(FriendshipStatus.ACCEPTED)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only view messages with friends."));
        }

        List<MessageEntity> allMessages = messageService.findConversationBetween(currentUser.getId(), targetUser.get().getId());

        List<MessageDto> response = allMessages.stream().map(MessageDto::fromEntity).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all-conversations")
    public ResponseEntity<?> getAllConversations(){
        User currentUser = getCurrentUser();
        List<ConversationSummaryDto> allConversations = messageService.findAllConversations(currentUser.getId());
        return ResponseEntity.ok(allConversations);
    }

    @DeleteMapping("/remove-conversation/{targetPublicId}")
    public ResponseEntity<?> removeConversation(@PathVariable String targetPublicId){
        User currentUser = getCurrentUser();

        Optional<User> targetUser = userService.findByPublicId(targetPublicId);

        if(targetUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Friendship> friendship = friendshipService.findFriendshipBetween(currentUser, targetUser.get());

        if(friendship.isEmpty() || !friendship.get().getStatus().equals(FriendshipStatus.ACCEPTED)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("Error", "You can delete the conversations only with friends."));
        }

        messageService.removeBySenderAndReceiver(currentUser.getId(), targetUser.get().getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
