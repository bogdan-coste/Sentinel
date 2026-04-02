package com.sentinel.dto.response;

import com.sentinel.entity.UserEntity;
import lombok.Data;

@Data
public class UserRes {
    private String publicId;
    private String name;
    private String username;
    private String profilePicUrl;
    private boolean privateUser;

    public static UserRes fromEntity(UserEntity userEntity) {
        if (userEntity == null) return null;
        UserRes dto = new UserRes();
        dto.setPublicId(userEntity.getPublicId());
        dto.setName(userEntity.getName());
        dto.setUsername(userEntity.getUsername());
        if (userEntity.getCurrentProfilePicURL() != null) {
            dto.setProfilePicUrl("/uploads/public/" + userEntity.getPublicId() + "/profile/" + userEntity.getCurrentProfilePicURL());
        }
        dto.setPrivateUser(userEntity.isPrivateUser());

        return dto;
    }
}