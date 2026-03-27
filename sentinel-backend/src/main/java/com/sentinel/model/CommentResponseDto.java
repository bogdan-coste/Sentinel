package com.sentinel.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserDto author;

    public static CommentResponseDto fromEntity(CommentEntity comment) {
        if (comment == null) return null;
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAuthor(UserDto.fromEntity(comment.getAuthor()));
        return dto;
    }
}
