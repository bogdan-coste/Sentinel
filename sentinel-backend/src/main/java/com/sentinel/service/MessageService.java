package com.sentinel.service;

import com.sentinel.dto.response.ConvSummaryRes;
import com.sentinel.dto.response.MessageRes;
import com.sentinel.dto.response.UserRes;
import com.sentinel.entity.*;
import com.sentinel.repository.MessageRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class MessageService {

    private final MessageRepository repo;

    public MessageService(MessageRepository repo){
        this.repo = repo;
    }

    @Transactional
    public MessageEntity sendMessage(MessageEntity message) {
       return repo.save(message);
    }

    public List<MessageEntity> findConversationBetween(Long userId1, Long userId2) {
        return repo.findConversationBetween(userId1, userId2);
    }

    public boolean existsBySenderAndReceiver(Long user1Id, Long user2Id){
        return repo.existsBySenderAndReceiver(user1Id, user2Id);
    }

    private ConvSummaryRes buildConversationSummary(Long userId, UserEntity partner) {
        Pageable topOne = PageRequest.of(0, 1);
        return getConversationSummaryDto(userId, partner, topOne);
    }

    @NonNull
    private ConvSummaryRes getConversationSummaryDto(Long userId, UserEntity partner, Pageable topOne) {
        Page<MessageEntity> lastMessagePage = repo.findLastMessageBetween(userId, partner.getId(), topOne);
        MessageRes lastMessageRes = lastMessagePage.getContent().stream()
                .findFirst()
                .map(MessageRes::fromEntity)
                .orElse(null);

        long unreadCount = repo.findAllUnreadMessages(partner.getId(), userId);

        return new ConvSummaryRes(
                UserRes.fromEntity(partner),
                lastMessageRes,
                unreadCount
        );
    }

    @Transactional
    public void removeBySenderAndReceiver(Long user1Id, Long user2Id){
        repo.removeBySenderAndReceiver(user1Id, user2Id);
    }

    public List<ConvSummaryRes> findAllConversations(Long currentUserId) {
        List<MessageEntity> threads = repo.findLastMessagesForAllThreads(currentUserId);

        return threads.stream().map(message -> {
            UserEntity partner = message.getSender().getId().equals(currentUserId)
                    ? message.getReceiver()
                    : message.getSender();

            ConvSummaryRes res = new ConvSummaryRes();
            res.setUserEntity(UserRes.fromEntity(partner));
            res.setLastMessage(MessageRes.fromEntity(message));

            res.setUnreadCount((int) repo.findAllUnreadMessages(partner.getId(), currentUserId));

            return res;
        }).toList();
    }
}