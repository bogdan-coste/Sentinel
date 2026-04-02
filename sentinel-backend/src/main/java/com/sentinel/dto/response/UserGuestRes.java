package com.sentinel.dto.response;

import com.sentinel.entity.UserEntity;
import lombok.Data;

@Data
public class UserGuestRes {
    private String publicId;
    private String name;
    private String username;
    private String profilePicUrl;
    private String bannerPicUrl;
    private String description;
    private int friendCount;
    private boolean isPrivate;
    private boolean isFriend;

    public static UserGuestRes fromEntity(UserEntity userEntity, boolean isFriend) {
        if (userEntity == null) return null;
        UserGuestRes dto = new UserGuestRes();
        dto.setPublicId(userEntity.getPublicId());
        dto.setName(userEntity.getName());
        dto.setUsername(userEntity.getUsername());
        dto.setFriendCount(userEntity.getFriendCount());
        dto.setPrivate(userEntity.isPrivateUser());
        dto.setFriend(isFriend);

        if (userEntity.getCurrentBannerPicURL() != null) {
            dto.setBannerPicUrl("/uploads/public/" + userEntity.getPublicId() + "/banner/" + userEntity.getCurrentBannerPicURL());
        }

        if (userEntity.getCurrentProfilePicURL() != null) {
            dto.setProfilePicUrl("/uploads/public/" + userEntity.getPublicId() + "/profile/" + userEntity.getCurrentProfilePicURL());
        }

        return dto;
    }

}