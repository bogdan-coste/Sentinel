package com.sentinel.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeResponseDto {

    private Long id;
    private UserDto user;
    private LocalDateTime createdAt;

    public static LikeResponseDto fromEntity(LikeEntity like){
        if (like == null) return null;
        LikeResponseDto dto = new LikeResponseDto();
        dto.setId(like.getId());
        dto.setUser(UserDto.fromEntity(like.getUser()));
        dto.setCreatedAt(like.getCreatedAt());
        return dto;
    }
}
