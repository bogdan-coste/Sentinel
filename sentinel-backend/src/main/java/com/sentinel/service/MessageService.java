package com.sentinel.service;

import com.sentinel.model.*;
import com.sentinel.repository.MessageRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    @Transactional(readOnly = true)
    public List<ConversationSummaryDto> findAllConversations(Long userId) {
        List<User> partners = repo.findConversationPartners(userId);
        Pageable topOne = PageRequest.of(0, 1);

        return partners.stream()
                .map(partner -> {
                    return getConversationSummaryDto(userId, partner, topOne);
                })
                .collect(Collectors.toList());
    }

    private ConversationSummaryDto buildConversationSummary(Long userId, User partner) {
        Pageable topOne = PageRequest.of(0, 1);
        return getConversationSummaryDto(userId, partner, topOne);
    }

    @NonNull
    private ConversationSummaryDto getConversationSummaryDto(Long userId, User partner, Pageable topOne) {
        Page<MessageEntity> lastMessagePage = repo.findLastMessageBetween(userId, partner.getId(), topOne);
        MessageDto lastMessageDto = lastMessagePage.getContent().stream()
                .findFirst()
                .map(MessageDto::fromEntity)
                .orElse(null);

        long unreadCount = repo.findAllUnreadMessages(partner.getId(), userId);

        return new ConversationSummaryDto(
                UserDto.fromEntity(partner),
                lastMessageDto,
                unreadCount
        );
    }

    @Transactional
    public void removeBySenderAndReceiver(Long user1Id, Long user2Id){
        repo.removeBySenderAndReceiver(user1Id, user2Id);
    }
}