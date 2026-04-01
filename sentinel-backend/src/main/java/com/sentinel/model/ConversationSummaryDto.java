package com.sentinel.model;

import lombok.Data;

@Data
public class ConversationSummaryDto {

    private UserDto user;
    private MessageDto lastMessage;
    private long unreadCount;

    public ConversationSummaryDto(UserDto userDto, MessageDto lastMessageDto, long unreadCount) {
        this.user = userDto;
        this.lastMessage = lastMessageDto;
        this.unreadCount = unreadCount;
    }
}
