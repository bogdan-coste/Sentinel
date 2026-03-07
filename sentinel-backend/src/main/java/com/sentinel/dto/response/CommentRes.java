package com.sentinel.dto.response;

import com.sentinel.entity.CommentEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentRes {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserRes author;

    public static CommentRes fromEntity(CommentEntity comment) {
        if (comment == null) return null;
        CommentRes dto = new CommentRes();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAuthor(UserRes.fromEntity(comment.getAuthor()));
        return dto;
    }
}
