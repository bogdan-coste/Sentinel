package com.sentinel.dto.request;

import com.sentinel.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageReq {

    @NotBlank(message = "Receiver public ID is required")
    private String receiverPublicId;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 2000, message = "Content must not exceed 2000 characters")
    private String content;

    private MessageType type = MessageType.TEXT;

    private Long replyToId;
}