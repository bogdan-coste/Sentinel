package com.sentinel.dto.response;

import com.sentinel.entity.LikeEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeRes {

    private Long id;
    private UserRes user;
    private LocalDateTime createdAt;

    public static LikeRes fromEntity(LikeEntity like){
        if (like == null) return null;
        LikeRes dto = new LikeRes();
        dto.setId(like.getId());
        dto.setUser(UserRes.fromEntity(like.getUserEntity()));
        dto.setCreatedAt(like.getCreatedAt());
        return dto;
    }
}
