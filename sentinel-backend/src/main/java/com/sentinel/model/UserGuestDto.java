package com.sentinel.model;

import com.sentinel.model.User;
import lombok.Data;

@Data
public class UserGuestDto {
    private String publicId;
    private String name;
    private String username;
    private String profilePicUrl;
    private String bannerPicUrl;
    private String description;
    private int friendCount;
    private boolean isPrivate;
    private boolean isFriend;

    public static UserGuestDto fromEntity(User user, boolean isFriend) {
        if (user == null) return null;
        UserGuestDto dto = new UserGuestDto();
        dto.setPublicId(user.getPublicId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setFriendCount(user.getFriendCount());
        dto.setPrivate(user.isPrivateUser());
        dto.setFriend(isFriend);

        if (user.getCurrentBannerPicURL() != null) {
            dto.setBannerPicUrl("/uploads/public/" + user.getPublicId() + "/banner/" + user.getCurrentBannerPicURL());
        }

        if (user.getCurrentProfilePicURL() != null) {
            dto.setProfilePicUrl("/uploads/public/" + user.getPublicId() + "/profile/" + user.getCurrentProfilePicURL());
        }

        return dto;
    }

}