package com.sentinel.dto.response;

import com.sentinel.entity.MediaEntity;
import com.sentinel.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MediaRes {

    private Long id;
    private String type;
    private String imageUrl;
    private int likeCount;
    private LocalDateTime createdAt;
    private String content;
    private boolean likedByCurrentUser;
    private int commentCount;

    public static MediaRes fromEntity(MediaEntity media) {
        if (media == null) return null;
        MediaRes dto = new MediaRes();
        dto.setId(media.getId());
        dto.setType(media.getType().toString());
        UserEntity owner = media.getOwner();
        if (owner!= null && media.getPath() != null) {
            String filename = extractFilename(media.getPath());
            if (filename != null) {
                String subfolder = switch (media.getType()) {
                    case PROFILE_PICTURE -> "profile";
                    case PROFILE_BANNER -> "banner";
                    case POST -> "posts";
                };
                String publicUrl = "/uploads/public/" + owner.getPublicId() + "/" + subfolder + "/" + filename;
                dto.setImageUrl(publicUrl);
            }
        }
        dto.setLikeCount(media.getLikeCount());
        dto.setCreatedAt(media.getCreatedAt());
        dto.setContent(media.getContent());
        return dto;
    }

    private static String extractFilename(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash != -1 ? path.substring(lastSlash + 1) : null;
    }
}
