package com.sentinel.dto.response;

import com.sentinel.enums.MessageStatus;
import com.sentinel.enums.MessageType;
import com.sentinel.entity.MessageEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageRes {
    private Long id;
    private UserRes sender;
    private UserRes receiver;
    private String content;
    private MessageType type;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private MessageStatus status;

    public static MessageRes fromEntity(MessageEntity message){
        MessageRes dto = new MessageRes();
        dto.setId(message.getId());
        dto.setSender(UserRes.fromEntity(message.getSender()));
        dto.setReceiver(UserRes.fromEntity(message.getReceiver()));
        dto.setContent(message.getContent());
        dto.setType(message.getType());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReadAt(message.getReadAt());
        dto.setStatus(message.getStatus());

        return dto;
    }
}
