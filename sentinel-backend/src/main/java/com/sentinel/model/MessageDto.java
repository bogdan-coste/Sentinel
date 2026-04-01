package com.sentinel.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private UserDto sender;
    private UserDto receiver;
    private String content;
    private MessageType type;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private MessageStatus status;

    public static MessageDto fromEntity(MessageEntity message){
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSender(UserDto.fromEntity(message.getSender()));
        dto.setReceiver(UserDto.fromEntity(message.getReceiver()));
        dto.setContent(message.getContent());
        dto.setType(message.getType());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReadAt(message.getReadAt());
        dto.setStatus(message.getStatus());

        return dto;
    }
}
